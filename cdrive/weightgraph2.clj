(use '(incanter core io charts datasets))
(use '[clojure.string :only (join split)])

;; (def dpm [31 28 31 30 31 30 31 31 30 31 30 31])
;; (for [x (range 1 12)] (reduce + (subvec dpm 0 x)))
;;
;; (def months [0 31 59 90 120 151 181 212 243 273 304 334])

(defn to-millis [dates] (map #(.getTime %) dates))

;;(defn date-to-ms
;;  [yyyy-mm-dd]
;;  (let [[y m d] (map #(Integer/parseInt %) )) (split yyyy-mm-dd #"-")]
;;    (+ d
;;       (months (- m 1)))))
;;
;;(defn num-years-to-milliseconds [x]
;;     (* 365 24 60 60 1000 x))

(defn using-format [format] {:format format})

(defn- parse-date [str format]
  (.parse (java.text.SimpleDateFormat. format) str))

(defn string-to-date
  ([str] 
    (string-to-date str (using-format "yyyy-MM-dd")))
  ([str conversion-params] 
    (parse-date str (:format (merge (using-format "yyyy-MM-dd") conversion-params)))))

(def ds (read-dataset "file:///c|/tim/weight/test.txt" :delim \space :header true))
;; (with-data ds (view (line-chart :Date :Weight)))

(with-data ds (view (time-series-plot (to-millis (map string-to-date ($ :Date ds))) :Weight)))

 