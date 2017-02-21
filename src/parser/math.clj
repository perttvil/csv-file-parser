(ns parser.math)

(defn count-distance [x1 y1 x2 y2]
  (Math/sqrt
    (+
      (Math/pow (- x1 x2) 2)
      (Math/pow (- y1 y2) 2))))

(defn count-duration [t1 t2]
  (- t2 t1))

(defn count-speed [distance duration]
  (/ distance duration))

(defn count-value [{:keys [current previous]}]
  (cond
    (nil? previous) (merge current {:distance 0 :duration 0 :speed 0})
    :else (let [distance (count-distance (:x current) (:y current) (:x previous) (:y previous))
                duration (count-duration (:t previous) (:t current))
                speed (count-speed distance duration)]
            (merge current {:distance distance :duration duration :speed speed}))))

(defn count-values-for-id [entities]
  (let [curs (map  #(assoc {} :current %) entities)
        prevs (map  #(assoc {} :previous %) (cons nil (butlast entities)))
        pairs (map  #(into {} %) (map  vector curs prevs))]
    (map  count-value pairs)))

(defn count-values [all-entities]
  (pmap  count-values-for-id (map  #(sort-by :t %) all-entities)))