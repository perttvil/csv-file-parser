(ns parser.main
  (:require [parser.parser :refer :all]
            [parser.math :refer :all])
  (:gen-class)
  (:import (java.time LocalDateTime Duration)))

(defn do-work [in out]
  (let [phase1 (LocalDateTime/now)
        parsed (doall (parse in))
        phase2 (LocalDateTime/now)
        foobar1 (println (str "Parsing took: " (Duration/between phase1 phase2)))
        counted (doall (count-values-for-entities parsed))
        phase3 (LocalDateTime/now)
        foobar2 (println (str "Counting values took: " (Duration/between phase2 phase3)))]
    (write-file out counted)
    (println (str "Writing file took: " (Duration/between phase3 (LocalDateTime/now))))
    (System/exit 0)))

(defn -main
  [& args]
  (cond
    (= (count args) 2) (time (do-work (first args) (second args)))
    :else (do
            (.println *err* "Required arguments missing: <input-file> <output-file>")
            (System/exit 1))))