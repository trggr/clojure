;;
;; Displays person's weight chart
;;
;; usage: clj weight.clj [name],
;; where name - is either tim, natalya, zaya, or lydia

(use '(incanter core io charts datasets))

(def location "file:///c|/tim/weight/")
(def person (or (first *command-line-args*) "tim"))

(defn to-millis [dates]
  (map #(.getTime %) dates))

(defn using-format [format]
  {:format format})

(defn parse-date [str format]
  (.parse (java.text.SimpleDateFormat. format) str))

(defn string-to-date
  ([str] 
    (string-to-date str (using-format "yyyy-MM-dd")))
  ([str params] 
    (parse-date str (:format (merge (using-format "yyyy-MM-dd") params)))))

(def ds (read-dataset (str  location person ".txt")
                      :delim \space :header true))

(with-data ds
  (view (time-series-plot (to-millis (map string-to-date ($ :Date ds)))
                          :Weight)))
 