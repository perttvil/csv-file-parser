(defproject parser "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :resource-paths ["resources/"]
  :aot [parser.main]
  :main parser.main
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :aliases {"run" ["run" "-m" "parser.main" "./data/small-input.csv" "./data/small-output.csv"]})