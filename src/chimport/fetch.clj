(ns chimport.fetch
  (:refer-clojure :exclude [find])
  (:require
    [chimport.clubhouse.api :as ch]))

(defn find
  [f coll]
  (->> coll
       (filter f)
       (first)))

(defn load-config
  [file]
  (read-string (slurp file)))

(defn by-name-or-id
  [name-or-id data]
  (or (= (:name data) name-or-id)
      (= (:id data) (str name-or-id))))

(defn fetch-team-id
  [state config]
  (let [{:keys [team api-token]} config]
    (->> (ch/list-teams api-token)
         (find #(by-name-or-id team %))
         :id)))

(defn fetch-project-id
  [state config]
  (let [{:keys [project api-token]} config
        {:keys [team-id]} state]
    (->> (ch/list-projects api-token)
         (find #(and (by-name-or-id project %)
                     (= (:team_id %) team-id)))
         :id)))

(defn fetch-member-id
  [state config]
  (let [{:keys [owner api-token]} config]
    (->> (ch/list-members api-token)
         (find #(= (get-in % [:profile :mention_name]) owner))
         :id)))

(defn fetch-state-id
  [state config]
  (let [{:keys [api-token]} config
        {:keys [team-id]} state]
    (some->> (ch/list-workflows api-token)
             (take 1)
             (find #(= (:team_id %) team-id))
             (:states)
             (map #(select-keys % [:id :name]))
             (find #(= (:name %) "This Sprint"))
             (:id))))

(defn clubhouse-ids
  [state config]
  (println "Fetching required ClubHouse ids")
  (->> {:team-id #'fetch-team-id
        :project-id #'fetch-project-id
        :member-id #'fetch-member-id
        :state-id #'fetch-state-id}
       (reduce
        (fn [state [k f]]
          (assoc state k (f state config)))
        {})
       (merge state)))
