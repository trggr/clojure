(use '[clojure.string :exclude [replace reverse join]])

(def ncfile "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000002768-10-000019.nc")
(def ncfile1 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000015847-10-000026.nc")

;; this one doesn't work
;;(def a (clojure.xml/parse "file.nc"))

;; (defn read-ncfile
;;   [file]
;;   "Reads NC file into a seq, returns sequence of strings"
;;   (line-seq (clojure.java.io/reader file)))

(defn read-ncfile
  [file]
  "Reads NC file into a seq, returns sequence of strings"
  (slurp file))

    
    ;; (loop [acc [] limit 20 s (line-seq reader)]
    ;;   (let [e (first s)]
    ;; 	(if (or (zero? limit) (nil? e))
    ;; 	  acc
    ;; 	  (let [[ignore k v] (re-matches #"<(.+)>(.*)" e)]
    ;; 	    (recur (conj acc (keyword k) v)
    ;; 		   (dec limit)
    ;; 		   (rest s))))))))

(defn str->key-value
  [s]
  "Convert a string to a key-value pair"
;;  (let [[ignore k v] (re-matches #"<([A-Z].+)>(.*)" s)
  (let [[ignore k v] (re-matches #"<(/*[A-Z]+.+)>(.*)" s)
	key (when v (lower-case k))]
    [key v]))

(defn next-line
  [s]
  (first s))

;; (defn parse2
;;   [acc text]
;;   (println (format "%s >>>> %s" acc text))
;;   (if (empty? text)
;;       [acc []]
;;     (let [[key val] (str->key-value (next-line text))
;; 	  s (seq key)]
;;       (cond
;;        ;; end of embedded list
;;        (.startsWith key "/")  [acc (rest text)]
;;        ;; beginning of embedded list
;;        (empty? val) (let [[a r] (parse2 {} (rest text))]
;; 		      (parse2 (assoc acc (keyword key) a) r))
;;       ;; regular 
;;        :else
;;        (parse2 (assoc acc (keyword key) val)
;; 		 (rest text))))))

;; (defn parse3
;;   [text]
;;    (first (parse2 {} text)))

;; (def a (parse3 (take 300 (read-ncfile ncfile1))))

(defn parse2
  [acc text]
  (loop [indent "" coll text]
    (let [s (first coll)]
      (if-not s
	acc
	(let [[line node val eonode other] s]
	  (cond (and node (> (count val) 0))
		(do
		  (print (format "\n%s %s ---> [%s]" indent node val)))
		node
		(do
		  (print (format "\n%s %s" indent node)))
		eonode
		(do
		  (print (format "%s" eonode)))
		:else
		(do
		  (print (format "\n%s %s" indent other))))
	  (recur
	   (cond (and node (zero? (count val))) (str indent "  ")
		 node indent
		 eonode (chop (chop indent))
		 :else indent)
	   (rest coll)))))))
       
(defn parse3
  [text]
  (parse2 []
	  (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)"
	  text)))

(parse3 (read-ncfile ncfile))

(def test
     "
<A>
<B>b
 <C>
  <D>d
  <E>e
  <F>f
 </C>
<G>g
<H>
<xml?
 h
</xml?h>
</H>
")

(def testb
 (re-seq #"<(/*[A-Z]+.*)>\s*(.*?)" test))

(def testb
 (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)" test))

(def testb
 (re-seq #"\w+" test))

(doseq [x testb]
  (println x))
