
(defn tmp
  [f v]
  "Takes f and vector of vectors v. Returns a vector which is
   a result of applying f to corresponding elements from each vector.
   (tmp + [[1 2 3] [4 5 6]]) -> [5 7 9]"
  (apply map f v))

(defn f1
  [& args]
  (doseq [arg args]
    (println (format "dbg=%s" arg))))

(defn tmp1 [coll n]
  "Reverses the interleave process into n number of subsequences"
  (loop [acc [] s coll cur 0]
    (let [e (first s)]
      (if-not e
	acc
	(recur (assoc acc cur
		      (conj (if (> cur (dec (count acc))) [] (acc cur))
		       e))
	       (rest s)
	       (mod (inc cur) n))))))

