(defn test [coll]
  (print (format "first=%s\n" (first coll)))
  (print (format "rest=%s\n" (rest coll)))
  (print (format "next=%s\n" (next coll))))

(defn test1 [coll]
  (if (next coll)
    (cons (first coll) (test1 (rest coll)))
    (cons (first coll) nil)))

(defn test2 [coll]
  (cons (first coll)
    (if (next coll)
      (test2 (rest coll))
      nil)))

;;  (loop [acc '() a coll]
;;    (if (= a '())
;;      acc
;;      (recur
;;	(conj acc (first coll))
;;	(next coll)))))

(defn echo
  [coll]
  (loop [acc nil
	 elt coll]
    (println (format "%s %s" acc elt))
    (if (= '() elt)
      (reverse acc)
      (recur (cons (first elt) acc)
	     (rest elt)))))

