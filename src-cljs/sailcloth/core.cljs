(ns sailcloth.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [put! chan <!]]
            [om.core :as om]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as dom :include-macros ]))

(enable-console-print!)


(defn get-position! []
  (let [ch (chan)]
    (js/navigator.geolocation.getCurrentPosition
     (fn [loc] (put! ch [ (.. loc -coords -latitude)
                          (.. loc -coords -longitude) ])))
    ch))

(defn get-weather-data! [ lat lng ]
  (let [ch (chan)
        parse #(js->clj (js/JSON.parse (.. % -target -responseText))
                        :keywordize-keys true) ]

    (doto (js/XMLHttpRequest.)
      (.addEventListener "load" #(put! ch (parse %)))
      (.open "get" (str "/weather-data/" lat "/" lng))
      (.send))
    ch))



(def app-state (atom { :daily-forecast [] }))
(go (swap! app-state assoc :daily-forecast (<! (apply get-weather-data! (<! (get-position!))))))


(def blah (:hourly (first (:daily-forecast @app-state))))


(defn wind-graph [data owner]
  (reify
    om/IRender
    (render [_]
      (let [max-speed (* 1.1 (apply max 15 (map :windSpeed data)))
            graph-height 100
            graph-width 240]
        (dom/svg { :height graph-height
                   :width  graph-width
                   :viewBox (clojure.string/join " " [0 0 graph-width graph-height])
                   :preserveAspectRatio "none" }
                 (map (fn [ wind hour ]
                        (let [height (* (/ (:windSpeed wind) max-speed) graph-height)]
                          (dom/rect { :width 10
                                      :height height
                                      :x (* hour (/ graph-width 24))
                                      :y (- graph-height height)})))
                      data
                      (range)))))))


(defn wind-array [data owner]
  (reify
    om/IRender
    (render [_]
      (dom/div (om/build-all wind-graph (map :hourly (rest (:daily-forecast data))))))))


(om/root wind-array app-state { :target (js/document.getElementById "sailcloth-root") })

