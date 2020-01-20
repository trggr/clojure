(use '(incanter core io charts datasets))

(defn to-millis [dates] (map #(.getTime %) dates))

(defn using-format [format] {:format format})

(defn- parse-date [str format]
  (.parse (java.text.SimpleDateFormat. format) str))

(defn string-to-date
  ([str] 
    (string-to-date str (using-format "yyyy-MM-dd")))
  ([str conversion-params] 
    (parse-date str (:format (merge (using-format "yyyy-MM-dd") conversion-params)))))

(def ds (read-dataset "file:///c|/tim/weight/test.txt" :delim \space :header true))

(with-data ds (view (time-series-plot (to-millis (map string-to-date ($ :Date ds))) :Weight)))

 