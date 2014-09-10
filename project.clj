(defproject sailcloth "0.1.0-SNAPSHOT"
  :description "Sailcloth"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.8.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring "1.3.1"]
                 [http-kit "2.1.18"]
                 [compojure "1.1.8"]
                 [cheshire "5.3.1"]
                 [forecast-clojure "1.0.3"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [org.clojure/clojurescript "0.0-2322"]
                 [om "0.7.1"]
                 [prismatic/om-tools "0.2.3"]]

  :plugins [[lein-cljsbuild "1.0.3"]]
  ; :hooks [leiningen.cljsbuild]

  :uberjar-name "sailcloth-standalone.jar"
  :min-lein-version "2.0.0"
  :main sailcloth.core

  :source-paths ["src"]
  :profiles {:uberjar {:aot :all}}

  :cljsbuild
  { :builds
    [{ :id "dev"
       :source-paths [ "src-cljs" ]
       :compiler
       { :output-to "resources/public/sailcloth.js"
         :output-dir "resources/public/cljs"
         :optimizations :simple
         :pretty-print true
         :source-map "resources/public/sailcloth.js.map" }}
     { :id "release"
       :source-paths ["src"]
            :compiler {
              :output-to "sailcloth.min.js"
              :optimizations :advanced
              :pretty-print false
              :preamble ["react/react.min.js"]
              :externs ["react/externs/react.js"]}}]})
