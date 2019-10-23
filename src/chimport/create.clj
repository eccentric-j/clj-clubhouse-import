(ns chimport.create
  (:require
   [chimport.clubhouse.api :as clubhouse]))

(defn normalize-epic
  [new-epic config]
  (assoc new-epic
         :app_url (str "https://app.clubhouse.io/"
                       (:organization config)
                       "/epic/"
                       (:id new-epic))))

(defn epic
  [state config]
  (println "Creating epic" (:epic config) "in ClubHouse")
  (let [{:keys [member-id]} state
        {:keys [api-token]} config
        name (:epic config)
        new-epic (clubhouse/create-epic
                  api-token
                  {:name name
                   :owner_ids [member-id]})]
    (assoc state
           :epic-id (:id new-epic)
           :epic (normalize-epic new-epic config))))

(defn stories
  [state config]
  (println (str "Creating " (count (:stories state)) " stories in ClubHouse"))
  (let [{:keys [api-token]} config
        stories (clubhouse/create-stories
                 api-token
                 (:stories state))]
    (assoc state
           :stories stories)))
