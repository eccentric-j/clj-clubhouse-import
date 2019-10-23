(ns chimport.core
  (:refer-clojure :exclude [split find])
  (:require
    [clojure.pprint :refer [pprint print-table]]
    [clojure.string :refer [split trim]]
    [chimport.fetch :as fetch]
    [chimport.csv :as csv]
    [chimport.create :as create]))

(defn load-config
  [file]
  (read-string (slurp file)))

(defn format-output
  [state config]
  (let [{:keys [epic stories]} state]
    (println "Epic")
    (print-table [:id :name :app_url] [epic])
    (print "\n")
    (println "Stories")
    (print-table [:id :name :app_url] stories)))

(defn -main
  [input-edn]
  (println "Loading input " input-edn)
  (let [config (load-config input-edn)]
    (-> {}
        (fetch/clubhouse-ids config)
        (create/epic config)
        (csv/load-stories config)
        (create/stories config)
        (format-output config))))

(comment
  (def config (load-config "input.secret.edn"))
  (def state (fetch/clubhouse-ids {} config))
  (pprint (parse-csv-file state config))
  (-main "input.secret.edn"))
