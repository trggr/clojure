(ns cljhtp.core)
(require '[clj-http.client :as client])

;;(ns evolution
;;  (:use clojure.test))

(defn yahoo-quote
  "Get Yahoo! stock quote"
  [symb]
  (client/get "http://finance.yahoo.com/d/quotes.csv"
	      {:query-params {"s" symb
			      "f" "nkqwxyr1l9t5p4"}}))

(:body (yahoo-quote "cx"))

;; http://finance.yahoo.com/q?s=cx&ql=1