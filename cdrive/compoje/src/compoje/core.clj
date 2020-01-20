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
        hiccup.core, hiccup.form-helpers
        ring.adapter.jetty, ring.middleware.params,
	ring.middleware.session)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]))

(def todo (ref {}))

(defn home
  []
  (html
   [:h1 "Hello world!"]
   [:p
    [:a {:href "form"} "Add another task"]]))

(defn render-form
  []
  (html
   (form-to
    [:post "/form"] "Add task" [:br]
    (text-field :task)
    (submit-button "Add"))))

(defn handle-form
  [params]
  {:body     (html
              [:h1 (str "Added new task: " (params :task))]
              [:p [:a {:href "view"} "View all tasks"]])
   :session  {:tasks (params :task)}})

(defn view
  [session]
  (html
   [:h1 (str "Tasks: " (:tasks session) session)]
   [:p
    [:a {:href "form"} "Add another task"]]))

(defroutes course
  (GET "/"      [] (home))
  (GET "/form"  [] (render-form))
  (GET "/view"  {session :session} (view session))
  (POST "/form" {params :params} (handle-form params)))

(wrap! course :session)

(def app
     (handler/site course))
