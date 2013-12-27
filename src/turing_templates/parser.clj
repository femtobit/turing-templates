; Copyright 2013 Damian Hofmann
;
; This file is part of turing-templates.
;
; turing-templates is free software: you can redistribute it and/or modify
; it under the terms of the GNU General Public License as published by
; the Free Software Foundation, either version 3 of the License, or
; (at your option) any later version.
;
; turing-templates is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
; GNU General Public License for more details.
;
; You should have received a copy of the GNU General Public License
; along with turing-templates. If not, see <http://www.gnu.org/licenses/>.

(ns turing-templates.parser
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

(defn extract-attr
  "Returns the value of the given attribute from the parse tree,
   either as list or as single value.
   This function expects a semantically valid parse tree."
  [attr tree]
  (loop [t (rest tree)]
    (let [current   (first t)
          content   (rest current)
          attr-name (nth (first content) 1)]
      (if (= attr-name attr)
        (rest (first (rest content)))
        (recur (rest t))))))

(defn convert-transitions
  "Expects a sequence of transition nodes from the parse tree.
   Returns a list of transitions in the turing machine format."
  [ts]
  (map
    (fn [t]
      (filter
        (fn [x] (not (contains? #{:from :to :transition} x)))
        (flatten t)))
      ts))

(defn convert-tree
  "Converts the parse tree to a turing machine."
  [tree]
  (let [states  (extract-attr "states" tree)
        symbols (extract-attr "symbols" tree)
        start   (first (extract-attr "start" tree))
        end     (extract-attr "end" tree)
        ts      (convert-transitions (extract-attr "transitions" tree))]
    (tm/create states symbols start end ts)))

(defn parse
  "Parses input and returns the corresponding turing machine, if it is valid."
  [input]
  (-> input
    parse-tree
    convert-tree))

(defn parse-file
  "Parses the file and returns the corresponding turing machine, if it is valid."
  [filename]
  (parse (slurp filename)))

