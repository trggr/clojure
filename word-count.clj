;; Find top 10 most frequently used words in a file
(use 'clojure.contrib.str-utils)
(use 'clojure.string)

(def file-name "/tim/text.txt")

;; create a hash of words. Each word has a counter
(defn word-count [acc str]
  (let [w (first str)]
    (if (nil? w)
      acc
      (word-count
	(assoc acc w
	  (if (contains? acc w)
	    (+ 1 (acc w))
	    1))
	(rest str)))))

;;(def words
;;  (re-split #"\s+"
;;    (slurp file-name)))

(def words
  (re-split #"[\s\d|+-=]+"
    (lower-case (slurp file-name))))

;; take most frequently used words
(take 20
  (sort #(- (val %2) (val %1))
    (word-count {} words)))

;; top words 4 letter or longer
(doseq [[k v] (take 20
		(sort #(- (val %2) (val %1))
		  (word-count {}
		    (filter #(> (count %) 4) words))))]
  (println k))

;; take 10 less frequently used words
(take 10 (sort-by second (word-count {} words)))

