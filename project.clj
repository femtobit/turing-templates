(defproject turing-templates "0.1.0"
  :description "Compiles turing machine definitions to C++ templates."
  :url ""
  :license {:name "GPLv3"
            :url "http://www.gnu.org/licenses/gpl-3.0.html"}
  :dependencies [[org.clojure/clojure "1.5.1"] [instaparse "1.2.13"] [rhizome "0.2.0"]]
  :main ^:skip-aot turing-templates.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

