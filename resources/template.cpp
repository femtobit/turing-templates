/**
 * This is the implementation of a turing machine in the form of C++ templates.
 * Try to compile this file to run the Turing machine.
 *
 * Created by turing-templates,
 *   https://github.com/femtobit/turing-templates.
 * Based on "C++ Templates are Turing Complete" by Todd Veldhuizen,
 *   http://ubietylab.net/ubigraph/content/Papers/pdf/CppTuring.pdf.
 */

/** States **/
$STATES$

/** Alphabet **/
$SYMBOLS$



/* Tape (functional style list) */
struct Nil {};
template<class Head, class Tail>
struct Pair
{
    typedef Head head;
    typedef Tail tail;
};

/* Transition Function */
template<typename State, typename Symbol>
struct TransitionFunction { };
/** Transitions **/
$TRANSITIONS$


// Forward declaration
template<typename NextState, typename Action,
    typename Tape_Left, typename Tape_Current,
    typename Tape_Right,
    template<typename Q, typename Sigma> class Delta>
struct ApplyAction;


/* Configuration */

template<typename State,
    typename Tape_Left,
    typename Tape_Current,
    typename Tape_Right,
    template<typename Q, typename Sigma> class Delta>
struct Configuration
{
    typedef typename Delta<State,Tape_Current>::next_state next_state;
    typedef typename Delta<State,Tape_Current>::action     action;
    typedef typename ApplyAction<next_state, action, Tape_Left, Tape_Current, Tape_Right, Delta>::halted_configuration halted_configuration;
};


/* Default action: write to current tape cell */
template<typename NextState, typename Action,
    typename Tape_Left, typename Tape_Current,
    typename Tape_Right,
    template<typename Q, typename Sigma> class Delta>
struct ApplyAction
{
    typedef typename Configuration<NextState, Tape_Left,
    Action, Tape_Right, Delta>::halted_configuration halted_configuration;
};

/* Move read head left */
template<typename NextState,
    typename Tape_Left, typename Tape_Current,
    typename Tape_Right,
    template<typename Q, typename Sigma> class Delta>
struct ApplyAction<NextState, Left, Tape_Left, Tape_Current, Tape_Right, Delta>
{
    typedef typename Configuration<NextState,
    typename Tape_Left::tail,
    typename Tape_Left::head,
    Pair<Tape_Current,Tape_Right>,
    Delta>::halted_configuration
        halted_configuration;
};

/* Move read head right */
template<typename NextState, typename Tape_Left,
    typename Tape_Current, typename Tape_Right,
    template<typename Q, typename Sigma> class Delta>
    struct ApplyAction<NextState, Right, Tape_Left, Tape_Current, Tape_Right, Delta>
{
    typedef typename Configuration<NextState,
    Pair<Tape_Current,Tape_Left>,
    typename Tape_Right::head,
    typename Tape_Right::tail,
    Delta>::halted_configuration
        halted_configuration;
};

/*
 * Move read head right when there are no nonblank characters
 * to the right -- generate a new Blank symbol.
 */
template<typename NextState, typename Tape_Left,
    typename Tape_Current,
    template<typename Q, typename Sigma> class Delta>
struct ApplyAction<NextState, Right, Tape_Left, Tape_Current, Nil, Delta>
{
    typedef typename Configuration<NextState,
    Pair<Tape_Current,Tape_Left>,
    Blank, Nil, Delta>::halted_configuration
        halted_configuration;
};

/** End states **/
$END_STATES$


/** Start configuration **/
typedef Configuration<Q0, Nil, A, Pair<A,Pair<A,Nil> >, TransitionFunction>::halted_configuration Input;

