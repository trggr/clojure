(defn handler [request]
        {:status 200
         :headers {"Content-Type" "text/plain"}
         :body "Hello World"})

(use 'ring.util.serve)
(serve handler)
; (stop server)

