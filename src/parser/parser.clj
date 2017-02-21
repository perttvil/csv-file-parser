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

(defn- format-csv-row [e]
  (join ";"
        (-> (list)
            (conj (:speed e))
            (conj (:duration e))
            (conj (:distance e))
            (conj (:y e))
            (conj (:x e))
            (conj (:t e))
            (conj (:id e)))))

(defn write-file [out entities]
  (let [header "track_id;measure_time;x_coord;y_coord;relative_distance:relative_elapsed_time;relative_speed"
        data (conj (pmap format-csv-row (flatten entities)) header)]
    (spit out (join \newline data))))