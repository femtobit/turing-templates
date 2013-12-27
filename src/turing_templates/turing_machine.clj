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

(defn create [states symbols start end ts]
  "Returns a turing machine with given attributes."
  {:states states
   :symbols symbols
   :start start
   :end end
   :transitions ts})

(defn transition [tm state symb]
  "Returns the turing machines transition for given state and symbol
  or nil,if none exists."

  (let [ts (tm :transitions)]
    (first (filter (fn [t]
      (and
        (= (first t) state)
        (= (nth t 1) symb)))
      ts))))

