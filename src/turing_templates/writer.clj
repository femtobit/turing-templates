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
      ""
      parts)))

(defn valid-identifier
  ([input] (valid-identifier input ""))

  ([input prefix]
  (-> input
    normalize
    camel-case
    (add-prefix prefix))))


; Converter functions

(defn conv-state [state]
  (str "struct " (valid-identifier state "Q") " {};"))

(defn conv-states [tm]
  (let [states (tm :states)]
    (str
      "/* States */\n"
      (s/join "\n" (map conv-state states))
      "\n\n")))

(defn conv-symbol [symb]
  (str "struct " (valid-identifier symb) " {};"))

(defn conv-symbols [tm]
  (let [symbols (into '("left" "right" "blank") (tm :symbols))]
    (str
      "/* Alphabet */\n"
      (s/join "\n" (map conv-symbol symbols))
      "\n\n")))

(defn tm-to-cpp-templates [tm]
  "Expects a turing machine. Returns C++ source code containing the TM in
  template form, so that it will run when the C++ code is compiled and display
  its output as an error message."

  (str
    (conv-states tm)
    (conv-symbols tm)
    ;(conv-transitions tm)
  ))
