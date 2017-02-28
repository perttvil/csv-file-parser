(ns parser.parser
  (:require [clojure.string :refer [lower-case split trim split-lines join]]))

(def name-x "poslistx")
(def name-y "poslisty")
(def name-time "timelist")
(def name-id "trackid")

(defn- csv-line->list-of-values "Parse csv row separated by ',' into list of values"
  [row]
  (let [cols (split (lower-case row) #",")]
    (filter not-empty (map trim cols))))

(defn- list-of-values->map
  "Convert value list into a map with correct keys"
  [row]
  (let [name (first row)
        payload (map read-string (map #(clojure.string/replace % #"\"|\[|\]" "") (rest row)))]
    (cond
      (= name name-x) {:x payload}
      (= name name-y) {:y payload}
      (= name name-time) {:t payload}
      (= name name-id) {:id (first payload)}
      :else nil)))

(defn- data-row-to-map [id [x y t]]
  {:x x :y y :t t :id id})

(defn- join-data-points [[xm ym tm idm]]
  (let [xs (:x xm)
        ys (:y ym)
        ts (:t tm)
        id (:id idm)
        groups (map vector xs ys ts)]
    (map (partial data-row-to-map id) groups)))

(defn parse
  [in-file]
  (-> (slurp in-file)
      (split-lines)
      (#(pmap csv-line->list-of-values %1))
      (#(filter some? (pmap list-of-values->map %1)))
      (#(partition 4 %1))
      (#(pmap join-data-points %1))))

(defn ->seconds "Return integer part of double" [t]
  (int t))

(defn ->millis "Return decimal numbers from given double as integer" [t]
  (int (* (- (bigdec t) (->seconds t)) 1000)))

(defn- format-csv-row [csv-date e]
  (join ";"
        (-> (list)
            (conj (:angle e))
            (conj (:speed e))
            (conj (:duration e))
            (conj (:distance e))
            (conj (:y e))
            (conj (:x e))
            (conj (->millis (:t e)))
            (conj (->seconds (:t e)))
            (conj (:t e))
            (conj csv-date)
            (conj (:id e)))))

(defn write-file [out entities csv-date]
  (spit out "track_id;measure_date;measure_time;measure_time_seconds;measure_time_millis;x_coord;y_coord;distance;elapsed_time;speed;angle")
  (doseq [e-list entities]
    (spit out (join \newline (conj (map (partial format-csv-row csv-date) e-list) "")) :append true)))