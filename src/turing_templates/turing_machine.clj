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

(ns turing-templates.turing-machine)

(def example-tm
  {:states ["0" "1" "2"]
   :symbols ["a" "b"]
   :start "0"
   :end ["2"]
   :transitions
     [["0" "a", "b" "0" :right]
      ["0" "b", "a" "0" :right]
      ["0" "#", "#" "1" :left]
      ["1" "a", "a" "1" :left]
      ["1" "b", "b" "1" :left]
      ["1" "#", "#" "2" :right]]
   })

(defn create
  "Returns a turing machine with given attributes."
  [states symbols start end ts]
  {:states states
   :symbols symbols
   :start start
   :end end
   :transitions ts})

(defn transition
  "Returns the turing machines transition for given state and symbol
   or nil, if none exists."
  [tm state symb]
  (let [ts (tm :transitions)]
    (first (filter (fn [t]
      (and
        (= (first t) state)
        (= (nth t 1) symb)))
      ts))))

