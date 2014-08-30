(defproject sailcloth "0.1.0-SNAPSHOT"
  :description "Sailcloth"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.8.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring "1.3.1"]
                 [http-kit "2.1.18"]
                 [compojure "1.1.8"]
                 [cheshire "5.3.1"]
                 [forecast-clojure "1.0.3"]]

  :uberjar-name "sailcloth-standalone.jar"
  :min-lein-version "2.0.0"
  :main sailcloth.core

  :source-paths ["src"]
  :profiles {:uberjar {:aot :all}})
