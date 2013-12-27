(ns turing-templates.writer
  (:require [clojure.string :as s])
  (:require [turing-templates.turing-machine :as tm]))


; Helper functions

(defn add-prefix [input prefix]
  (if (not= (subs input 0 (count prefix)) prefix)
    (str prefix input)
    input))

(defn normalize [input]
    (s/replace input #"[^\w\s]" ""))

(defn camel-case [input]
  (let [parts (s/split input #"[\s\_]+")]
    (reduce
     (fn [result part]
        (str result (s/capitalize part)))
      "" parts)))

(defn valid-identifier
  ([input] (valid-identifier input ""))
  ([input prefix]
  (-> input
    normalize
    camel-case
    (add-prefix prefix))))

(defn into-template
  "Expects a template string and a map of key-value pairs.
   Returns the template with all occurences of '$key$' replaced by
   the corresponding value."
  [template dict]
  (reduce
    (fn [tpl entry]
      (s/replace tpl (str "$" (key entry) "$") (val entry)))
    template dict))


; Converter functions

(defn conv-states [tm]
  (let [states (tm :states)]
    (s/join "\n"
      (map
        (fn [state]
          (str "struct " (valid-identifier state "Q") " {};"))
        states))))

(defn conv-symbols [tm]
  (let [symbols (tm :symbols)]
    (s/join "\n"
      (map
        (fn [symb]
          (str "struct " (valid-identifier symb) " {};"))
        symbols))))

(def transition-template
  "template<>
struct TransitionFunction<$STATE$,$SYMBOL$>
{
  typedef $NEXT_STATE$ next_state;
  typedef $ACTION$ action;
};")

(defn conv-transitions [tm]
  (let [ts (tm :transitions)]
    (s/join "\n"
      (map
        (fn [t]
          (into-template transition-template
             {"STATE"      (valid-identifier (nth t 0) "Q")
              "SYMBOL"     (valid-identifier (nth t 1))
              "ACTION"     (valid-identifier (nth t 2))
              "NEXT_STATE" (valid-identifier (nth t 3) "Q")}))
        ts))))

(def end-state-template
  "template<typename Action, typename Tape_Left,
    typename Tape_Current, typename Tape_Right,
    template<typename Q, typename Sigma> class Delta>
struct ApplyAction<$STATE$, Action, Tape_Left, Tape_Current, Tape_Right, Delta>
{
  // Halt
  // Since halted_configuration is not declared, this will cause a compiler error
};")

(defn conv-end-states [tm]
  (let [end-states (tm :end)]
    (s/join "\n"
      (map
        (fn [state]
          (into-template end-state-template
            {"STATE" (valid-identifier state "Q")}))
        end-states))))

; Functions to prepare the turing machine

(defn prepare-transition [t]
  (let [current-state (nth t 0)
        read-symbol   (s/replace (nth t 1) #"#" "blank")
        write-symbol  (s/replace (nth t 2) #"#" "blank")
        next-state    (nth t 3)
        direction     (nth t 4)
        intermediate-state (str next-state " " (name direction))]

    [[current-state read-symbol, write-symbol intermediate-state :ignore]
     [intermediate-state write-symbol, (name direction) next-state :ignore]]))

(defn prepare-tm [tm]
  (let
    [new-states
      (reduce
        (fn [previous-states state]
          (conj previous-states state (str state " left") (str state " right")))
        [] (tm :states))
     new-symbols
       (conj (tm :symbols) "left" "right" "blank")
     new-transitions
       (reduce
         (fn [ts t]
           (into ts (prepare-transition t)))
         [] (tm :transitions))]
    (tm/create new-states new-symbols (tm :start) (tm :end) new-transitions)))


; Main function

(defn convert-turing-machine [turing-machine]
  "Expects a turing machine. Returns C++ source code containing the TM in
  template form, so that it will run when the C++ code is compiled and display
  its output as an error message."
  (let [tm (prepare-tm turing-machine)
        template (slurp (clojure.java.io/resource "template.cpp"))]
    (into-template template
      {"STATES"      (conv-states tm)
       "SYMBOLS"     (conv-symbols tm)
       "TRANSITIONS" (conv-transitions tm)
       "END_STATES"  (conv-end-states tm)})))

