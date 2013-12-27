(ns turing-templates.core
  (:require [instaparse.core :as insta])
  (:require [turing-templates.turing-machine :as tm])
  (:require [turing-templates.parse :as parse])
  (:require [turing-templates.writer :as writer])
  (:gen-class))


(defn -main [& args]
  (if (not= (count args) 2)
    (println (str "Usage: java -jar turing-templates*.jar infile outfile"))
    (let [infile  (nth args 0)
          outfile (nth args 1)]
      (spit outfile (writer/convert-turing-machine (parse/parse-file infile))))))

