(ns chimport.clubhouse.api
  (:require
    [clj-http.client :as client]
    [cheshire.core :as json]))

(defn get!
  [resource token]
  (-> (client/get
        (str "https://api.clubhouse.io/api/v3/" resource)
        {:query-params {"token" token}})
      (:body)
      (json/parse-string true)))

(defn create!
  [resource token body]
  (-> (client/post
       (str "https://api.clubhouse.io/api/v3/" resource)
       {:query-params {"token" token}
        :form-params body
        :content-type :json})
      (:body)
      (json/parse-string true)))

(defn list-projects
  [token]
  (get! "projects" token))

(defn list-teams
  [token]
  (get! "teams" token))

(defn list-members
  [token]
  (get! "members" token))

(defn list-workflows
  [token]
  (get! "workflows" token))

(defn create-epic
  [token epic]
  (create! "epics" token epic))

(defn create-stories
  [token stories]
  (create! "stories/bulk" token {:stories stories}))

(comment
  (require '[chimport.core :refer [load-config]])
  (def config (load-config "input.secret.edn"))
  (println config)
  (fetch-team-id config)
  (clojure.pprint/pprint
   (fetch-requirements {} config)))
