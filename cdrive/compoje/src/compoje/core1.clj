;; Hello world in Compojure. To run:
;;   lein ring server
;;   or
;;   lein ring server-headless 3000
;; then browse
;;    http://127.0.0.1:3000/
;;    or
;;    http://127.0.0.1:3000/away
(ns compoje.core
  (:use compojure.core)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]))

(defroutes main-routes
  (GET "/" [] "<h1>Hello, world wide web!</h1>")
  (GET "/away" [] "<h1>Hello, away!</h1>")
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
     (handler/site main-routes))
