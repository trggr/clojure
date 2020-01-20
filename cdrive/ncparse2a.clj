;;;; ---------------------------------
;;;;
;;;; Prints NC file with indentations
;;;;
;;;; ---------------------------------
(use '[clojure.string :exclude [replace reverse join]])
(use 'clojure.contrib.str-utils)

(def *debug* nil)

(defn valuable?
  [val]
  (not (empty? val)))

(defn kword
  [node]
  "Turns node into a keyword"
  (->> node
       chop
       rest
       (apply str)
       lower-case
       keyword))

(defn dprintln
  [& more]
  "Prints output only when *debug* variable is set"
  (if *debug*
    (apply println more)))

(defn read-ncfile
  [file]
  "Returns a string with file contents"
  (slurp file))

(defn line-class
  [line]
  "Classify a an input line"
  (let [[ignore node val stop multival] line]
    (cond (and node (valuable? val)) [:node node val]
	  stop                       [:stop stop]
	  node                       [:branch node]
	  multival                   [:multival (trim multival)]
	  :else                      [:other line])))

(defn append-to-last
  [arr s]
  "Appends string s to the last element of array"
  (conj (vec (butlast arr)) (str (last arr) s)))

(defn class-type [x] (first x))
(defn class-name [x] (second x))
(defn class-val [x] (nth x 2))

;;-----
(let [v (ref false)
      text-flag-set (fn [x] (dosync (ref-set v x)))]
  (defn text-flag-curval [] @v)
  (defn text-flag-reset [] (text-flag-set false))
  (defn text-flag-flip [] (text-flag-set (not @v))))

;;
;; Stack implementation
;;
(let [stack (ref '())
      set-stack (fn [x] (dosync (ref-set stack x)))]
  (defn push [x]
    (set-stack (conj @stack x)))
  (defn pop []
    (let [x (first @stack)]
      (set-stack (rest @stack))
      x))
  (defn empty-stack? []
    (empty? @stack))
  (defn get-stack []
    @stack)
  (defn reset-stack []
    (set-stack '())))

(defn between-text? [x]
  "Returns true, when x is between <TEXT> and </TEXT>"
  (let [y (first x)]
    (cond (and (= y :branch) (= (second x) "<TEXT>"))
	  (do
	    (text-flag-flip))
	  (and (= y :stop) (= (second x) "</TEXT>"))
	  (do (text-flag-flip)
	      true)
	  :else (text-flag-curval))))

;; (defn parse2
;;   [acc text]
;;   (loop [indent "" coll text]
;;     (let [s (first coll)]
;;       (if-not s
;; 	acc
;; 	(let [[line node val eonode other] s]
;; 	  (cond (and node (> (count val) 0))   ;; node with value
;; 		(print (format "\n%s %s ---> [%s]" indent node val))
;; 		node
;; 		(print (format "\n%s %s" indent node))
;; 		eonode
;; 		(print (format "%s" eonode))
;; 		:else
;; 		(print (format "\n%s %s" indent other)))
;; 	  (recur
;; 	   (cond (and node (zero? (count val))) (str indent "  ")
;; 		 node indent
;; 		 eonode (chop (chop indent))
;; 		 :else indent)
;; 	   (rest coll)))))))

;; (defn parse2
;;   [acc text]
;;   (loop [indent "" coll text]
;;     (let [s (first coll)]
;;       (if-not s
;; 	acc
;; 	(let [[line node val eonode other] s]
;; 	  (cond (and node (> (count val) 0))   ;; node with value
;; 		(print (format "\n%s %s ---> [%s]" indent node val))
;; 		node
;; 		(print (format "\n%s %s" indent node))
;; 		eonode
;; 		(print (format "%s" eonode))
;; 		:else
;; 		(print (format "\n%s %s" indent other)))
;; 	  (recur
;; 	   (cond (and node (zero? (count val))) (str indent "  ")
;; 		 node indent
;; 		 eonode (chop (chop indent))
;; 		 :else indent)
;; 	   (rest coll)))))))

(defn parse2
  [text]
  (reset-stack)
  (loop [acc [] coll text]
    (let [s (first coll)]
      (println (format "\nInput = %s, ssize = %s, asize = %s " s (count (get-stack)) (count acc)))
      (println (take 4 (get-stack)))
      (println (take 4 acc))
      (if-not s
	(if (empty-stack?) acc (vector (pop) acc))
	(let [rtype (first s)
	      tag (second s)]
	  (cond (= :node rtype)     (recur (conj acc (kword tag) (class-val s))
					   (rest coll))
		(= :stop rtype)     (let [v (vector (pop) acc)]
				      (dprintln (format "***** %s" v))
				      (recur v (rest coll)))
		(= :branch rtype)   (do
				      (push (if (empty? acc)
					      (kword tag)
					      (vector (kword tag) acc)))
				      (recur [] (rest coll)))
		(= :multival rtype) (recur (append-to-last acc (second s))
					   (rest coll))
		:else               (assert false)))))))

;; (defn parse3
;;   [text]
;;   "Parses a string into a tree"
;;   (parse2 (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)"
;; 	  text)))

;; (parse3 (read-ncfile ncfile1))

(defn tokenize
  [text]
  "Tokenizes an input string into fields: node value branch multival"
  (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)" text))
	  
  

(def ncfile "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000002768-10-000019.nc")
(def ncfile1 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000015847-10-000026.nc")
(def ncfile2 "/tim/clojure/nctest.txt")

(def test-string
"<SUBMISSION>
<SUBMITTER>
<PERSON>
<NAME>Tim
<LNAME>Ashchepkov
</PERSON>
<LOCATION>
<CITY>Simsbury
<STATE>CT
</LOCATION>
</SUBMITTER>
<TEXT>
Line 1
Line 2
</TEXT>
</SUBMISSION>
")

(defn run []
  (text-flag-reset)
  (reset-stack)
  (let [x (map line-class (tokenize (read-ncfile ncfile2)))
	text-only (filter  between-text? x)
	wo-text (filter (complement between-text?) x)]
    (parse2 wo-text)))

  
