(ns hello-ring.core)

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello Ring"})

(defn what-is-my-address [request]
  {:status  200
   :headers {"Content-Type" "text/plain"}
   :body    (:remote-addr request)})


