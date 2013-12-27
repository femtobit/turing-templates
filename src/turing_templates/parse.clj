(ns turing-templates.parse
  (:require [instaparse.core :as insta])
  (:require [turing-templates.turing-machine :as tm]))

(def turing-machine-parser
  (insta/parser
    "
    turing-machine  ::= (<ws> definition <';'> <ws>)*
    definition      ::= name <ws> <'='> <ws> expr
    expr            ::= list | value
    <list>          ::= <'{'> <ws> value (<ws> <','> <ws> value)* <ws> <'}'>
    <value>         ::= symbol | list | transition
    transition      ::= from <ws> <'->'> <ws> to
    from            ::= <'('> <ws> symbol <ws> <','> <ws> symbol <ws> <')'>
    to              ::= <'('> <ws> symbol <ws> <','> <ws> symbol <ws> <','> <ws> dir <ws> <')'>
    <dir>           ::= left | right
    left            ::= <'l'> | <'L'> | <'left'>
    right           ::= <'r'> | <'R'> | <'right'>
    name            ::= #'\\w+'
    <symbol>        ::= #'[\\w#]+'
    ws              ::= #'\\s'*
    "))

(defn parse-tree [input]
  (turing-machine-parser input))

(defn extract-attr [attr tree]
  "Returns the value of the given attribute from the parse tree,
   either as list or as single value.
   This function expects a semantically valid parse tree."

  (loop [t (rest tree)]
    (let [current   (first t)
          content   (rest current)
          attr-name (nth (first content) 1)]
      (if (= attr-name attr)
        (rest (first (rest content)))
        (recur (rest t))))))

(defn convert-transitions [ts]
  "Expects a sequence of transition nodes from the parse tree.
   Returns a list of transitions in the turing machine format."
  (map
    (fn [t]
      (filter
        (fn [x] (not (contains? #{:from :to :transition} x)))
        (flatten t)))
      ts))

(defn convert-tree [tree]
  "Converts the parse tree to a turing machine."

  (let [states  (extract-attr "states" tree)
        symbols (extract-attr "symbols" tree)
        start   (first (extract-attr "start" tree))
        end     (extract-attr "end" tree)
        ts      (convert-transitions (extract-attr "transitions" tree))]
    (tm/create states symbols start end ts)))

(defn parse [input]
  "Parses input and returns the corresponding turing machine if it is valid."
  (-> input
    parse-tree
    convert-tree))

(defn parse-file [filename]
  "Parses the file and returns the corresponding turing machine if it is valid."
  (parse (slurp filename)))

