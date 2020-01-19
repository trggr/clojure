(defproject compoje "1.0.0-SNAPSHOT"
  :description "Compojure app"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [compojure "0.6.0-RC4"]]
  :dev-dependencies [[lein-ring "0.3.2"]]
  :ring {:handler compoje.core/app})
