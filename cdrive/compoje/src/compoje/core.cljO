;; Hello world in Compojure. To run:
;;   lein ring server
;;   or
;;   lein ring server-headless 3000
;; then browse
;;    http://127.0.0.1:3000/
;;    or
;;    http://127.0.0.1:3000/away
(ns compoje.core
  (:use compojure.core
        hiccup.core,hiccup.form-helpers
        ring.adapter.jetty, ring.middleware.params,
	ring.middleware.session)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]))

(defn display []
  (html [:h1 "hello world!"]))

(defn render-form []
  (html
   (form-to
    [:post "/form"] "Add task" [:br]
    (text-field :task)
    (submit-button "save"))))

(defn handle-form
  [params]
  {:body (str "stored" (params :task))
   :session {:tasks (params :task)}})

(defn view
  [session]
  (html [:h1 (str "tasks: " (:tasks session))]))

(defroutes myroutes
  (GET "/" [] (display))
  (GET "/form" [] (render-form))
  (GET "/view" {session :session} (view session))
  (POST "/form" {params :params} (handle-form params)))

(wrap! myroutes :session)

(def app
     (handler/site myroutes))
