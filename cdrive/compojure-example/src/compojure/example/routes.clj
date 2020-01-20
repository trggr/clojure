(ns compojure.example.routes
  (:use compojure.core
        compojure.example.views
        [hiccup.middleware :only (wrap-base-url)])
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.response :as response]))

(defroutes main-routes
  (GET "/"         []       (index-page))
  (GET "/user/:id" [id]     (str "<h1>Hello user " id "</h1>"))
  
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site main-routes)
      (wrap-base-url)))

;; how to start server
(use 'ring.util.serve)
(serve app)
(stop-server)

; ---- project.clj ----
;(defproject compojure-example "0.1.0"
;  :description "Example Compojure project"
;  :dependencies [[org.clojure/clojure "1.2.0"]
;                 [compojure "0.6.2"]
;                 [hiccup "0.3.4"]]
;  :dev-dependencies [[ring-serve "0.1.1"]]
;  :ring {:handler compojure.example.routes/app})

