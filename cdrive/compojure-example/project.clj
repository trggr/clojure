(defproject compojure-example "0.1.0"
  :description "Example Compojure project"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [compojure "0.6.2"]
                 [hiccup "0.3.4"]]
  :dev-dependencies [[ring-serve "0.1.1"]]
  :ring {:handler compojure.example.routes/app})
