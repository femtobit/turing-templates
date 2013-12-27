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

(ns turing-templates.core
  (:require [turing-templates.parser :as parser])
  (:require [turing-templates.writer :as writer])
  (:gen-class))


(defn -main [& args]
  (if (not= (count args) 2)
    (println (str "Usage: java -jar turing-templates*.jar infile outfile"))
    (let [infile  (nth args 0)
          outfile (nth args 1)]
      (spit outfile (writer/convert-turing-machine (parser/parse-file infile))))))

