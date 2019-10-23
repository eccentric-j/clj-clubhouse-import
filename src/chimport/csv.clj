(ns chimport.csv
  (:refer-clojure :exclude [split])
  (:require
    [clojure.data.csv :as csv]
    [clojure.string :refer [split starts-with? trim]]))

(defn load-csv-file
  [file]
  (with-open [rdr (clojure.java.io/reader file)]
    (into [] (csv/read-csv rdr))))

(defn csv->map
  [columns config]
  (let [{:keys [story-column estimate-column]} config]
    {:name (get columns story-column)
     :estimate (get columns estimate-column)}))

(defn format-story
  [story state config]
  (let [{:keys [epic-id member-id project-id state-id]} state
        {:keys [name estimate]} story]
    (merge story
           {:epic_id epic-id
            :estimate (Integer. estimate)
            :project_id project-id
            :owner_ids [member-id]
            :requested_by_id member-id
            :workflow_state_id state-id
            :story_type (if (starts-with? story "User")
                          :feature
                          :chore)})))

(defn load-stories
  [state config]
  (println "Processing CSV file")
  (->> config
       (:csv)
       (load-csv-file)
       (drop 1)
       (map #(csv->map % config))
       (remove #(empty? (trim (:name %))))
       (map #(format-story % state config))
       (assoc state :stories)))

(comment
  (load-csv-file "stories.secret.csv"))
