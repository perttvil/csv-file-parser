(ns parser.math)

(defn count-distance [x2 y2 x1 y1]
  (Math/sqrt
    (+
      (Math/pow (- x2 x1) 2)
      (Math/pow (- y2 y1) 2))))

(defn count-duration [t2 t1]
  (- t2 t1))

(defn count-speed [distance duration]
  (/ distance duration))

(defn count-angle [x2 y2 x1 y1]
  (let [x-delta (- (bigdec x2) (bigdec x1))
        y-delta (- (bigdec y2) (bigdec y1))
        angle (delay (Math/toDegrees (Math/atan (.abs (.divide y-delta x-delta 8 java.math.RoundingMode/HALF_DOWN)))))]
    (cond
      (and (zero? x-delta) (neg? y-delta)) 180              ;; Straight down => 180
      (zero? x-delta) 0                                     ;; Straight up => 0
      (and (pos? x-delta) (pos? y-delta)) (- 90 @angle)     ;; First quarter of a circle
      (pos? x-delta) (+ 90 @angle)                          ;; Second quarter of a circle
      (neg? y-delta) (- 270 @angle)                         ;; Third quarter of a circle
      :else (+ 270 @angle)                                  ;; Fourth quarter of a circle
      )))

(defn count-values [{:keys [current previous]}]
  (cond
    (nil? previous) (merge current {:distance 0 :duration 0 :speed 0 :angle 0})
    :else (let [distance (count-distance (:x current) (:y current) (:x previous) (:y previous))
                duration (count-duration (:t current) (:t previous))
                speed (count-speed distance duration)
                angle (count-angle (:x current) (:y current) (:x previous) (:y previous))]
            (merge current {:distance distance :duration duration :speed speed :angle angle}))))

(defn count-values-for-id [entities]
  (let [curs (map #(assoc {} :current %) entities)          ;; Current values
        prevs (map #(assoc {} :previous %) (cons nil (butlast entities))) ;; List of previous values to count diff from
        pairs (map #(into {} %) (map vector curs prevs))]   ;; List of pairs, current and previous to diff to
    (map count-values pairs)))

(defn count-values-for-entities [all-entities]
  (pmap count-values-for-id (map #(sort-by :t %) all-entities)))