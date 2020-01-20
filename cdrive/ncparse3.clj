(use '[clojure.string :exclude [replace reverse join]])

(def ncfile "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000002768-10-000019.nc")
(def ncfile1 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000015847-10-000026.nc")

;; this one doesn't work
;;(def a (clojure.xml/parse "file.nc"))

(defn read-ncfile
  [file]
  "Reads NC file into a seq, returns sequence of strings"
  (slurp file))

(defn valuable?
  [val]
  (not (empty? val)))

(defn kword
  [node]
  "Turns node into a keyword"
  (keyword (lower-case node)))

(defn parse2
  [acc text]
  (if-not text
    [acc nil]
    (let [[line node val eonode other] (first text)]
      (cond (and node (valuable? val))
	    (parse2 (assoc acc (kword node) val) (rest text))
	    node
	    (let [[branch unprocessed-text] (parse2 {} (rest text))]
	      (parse2 (assoc acc (kword node) branch) unprocessed-text))
	    eonode
	    [acc (rest text)]
	    other
	    (do (print (format "\n other: %s" other))
		[acc (rest text)])
	    :else
	    (do (print (format "\n other: %s" other))
		[acc (rest text)])))))

;; (defn parse2
;;   [acc text]
;;   (loop [indent "" coll text]
;;     (let [s (first coll)]
;;       (if-not s
;; 	acc
;; 	(let [[line node val eonode other] s]
;; 	  (cond (and node (> (count val) 0))
;; 		(do
;; 		  (print (format "\n%s %s ---> [%s]" indent node val)))
;; 		node
;; 		(do
;; 		  (print (format "\n%s %s" indent node)))
;; 		eonode
;; 		(do
;; 		  (print (format "%s" eonode)))
;; 		:else
;; 		(do
;; 		  (print (format "\n%s %s" indent other))))
;; 	  (recur
;; 	   (cond (and node (zero? (count val))) (str indent "  ")
;; 		 node indent
;; 		 eonode (chop (chop indent))
;; 		 :else indent)
;; 	   (rest coll)))))))

(defn parse3
  [text]
  "Parses a string into a tree"
  (parse2 {}
	  (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)"
	  text)))

(def a (parse3 (read-ncfile ncfile1)))

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
 (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)" test))

