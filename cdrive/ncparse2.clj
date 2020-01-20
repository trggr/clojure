;;;; ---------------------------------
;;;;
;;;; Prints NC file with indentations
;;;;
;;;; ---------------------------------
(use '[clojure.string :exclude [replace reverse join]])
(use 'clojure.contrib.str-utils)

(def ncfile "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000002768-10-000019.nc")
(def ncfile1 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000015847-10-000026.nc")

(defn read-ncfile
  [file]
  "Returns a string with file contents"
  (slurp file))

(defn parse2
  [acc text]
  (loop [indent "" coll text]
    (let [s (first coll)]
      (if-not s
	acc
	(let [[line node val eonode other] s]
	  (cond (and node (> (count val) 0))   ;; node with value
		(print (format "\n%s %s ---> [%s]" indent node val))
		node
		(print (format "\n%s %s" indent node))
		eonode
		(print (format "%s" eonode))
		:else
		(print (format "\n%s %s" indent other)))
	  (recur
	   (cond (and node (zero? (count val))) (str indent "  ")
		 node indent
		 eonode (chop (chop indent))
		 :else indent)
	   (rest coll)))))))

(defn parse3
  [text]
  "Parses a string into a tree"
  (parse2 {}
	  (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)"
	  text)))

(parse3 (read-ncfile ncfile1))
