(ns sailcloth.core
  (:require [compojure.core        :refer [defroutes GET]]
            [compojure.route       :refer [resources not-found]]
            [org.httpkit.server    :refer [ run-server ]]
            [ring.util.response    :refer [file-response]]
            [forecast-clojure.core :refer [forecast] ]
            [cheshire.core         :refer :all]
            [clj-time.periodic     :as p ]
            [clj-time.local        :as lt ]
            [clj-time.core         :as t ]
            [clj-time.coerce       :as tc ]
            [compojure.handler     :as handler ]))

(defn get-tz [s]
  (cond
   (instance? java.lang.String s) (org.joda.time.DateTimeZone/forID s)
   (instance? org.joda.time.DateTimeZone s) s
   :else nil))

(defn secs->date
  ([t tz] (t/to-time-zone (secs->date t) (get-tz tz)))
  ([t] (tc/from-long (* t 1000))))

(defn prepare-hourly-data [d]
  (let [ tz (get-tz (:timezone d))]
    (->> d :hourly :data
         (map #(select-keys % [:windSpeed :windBearing :apparentTemperature :icon :summary :time]))
         (partition-by #(.getDayOfWeek (secs->date (:time %) tz)))
         (butlast))))

(defn prepare-daily-data [d]
  (->> d :daily :data
       (map #(select-keys % [:apparentTemperatureMax :apparentTemperatureMaxTime
                             :apparentTemperatureMin :apparentTemperatureMinTime
                             :windSpeed :windBearing :time :icon :summary]))))

(defn get-data! [latitude longitude]
  (let [forecast-data (forecast (str latitude) (str longitude) :params { :extend "hourly" })]
    (map #(assoc %1 :hourly %2)
         (prepare-daily-data forecast-data)
         (prepare-hourly-data forecast-data))))

(defroutes routes
  (GET "/" [] (file-response "index.html" { :root "public" }))
  (GET "/weather-data/:latitude/:longitude" [latitude longitude]
       { :headers { "Content-Type" "text/json" }
         :body (generate-string (get-data! latitude longitude)) })
  (resources "/")
  (not-found "<h1>Page not found</h1>"))

(defn -main [ & [port] ]
  (let [ port (Integer/parseInt (or (System/getenv "PORT") port "8080")) ]

    (println (str "Starting on port: " port))
    (run-server (handler/site routes) { :port port :join? false })))
