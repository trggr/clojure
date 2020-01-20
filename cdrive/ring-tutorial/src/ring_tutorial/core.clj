(ns ring-tutorial.core
  (:use ring.adapter.jetty)
  (:use ring.middleware.reload)
  (:use ring.middleware.stacktrace))

(defn handler [req]
  {:status 200
    :headers {"Content-Type" "text/html"}
    :body "Hello world from Ring-3"})

(def app
  (-> #'handler
    (wrap-reload '(ring-tutorial.core))
    (wrap-stacktrace)))

(defn boot []
  (run-jetty #'app {:port 8080}))
