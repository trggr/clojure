;;;; ---------------------------------
;;;;
;;;; Prints NC file with indentations
;;;;
;;;; ---------------------------------
(use '[clojure.string :exclude [replace reverse join]])
(use 'clojure.contrib.str-utils)

(def DEBUG nil)

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
  (if DEBUG
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
  (defn push+ [x]
    (set-stack (conj @stack x)))
  (defn pop+ []
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

(defn nvl [x val] (if x x val))

(defn parse2
  [text]
  (push+ text)
  (loop [acc []]
    (let [t (pop+)
	  s (first t)]
      (if-not s
	acc
	(let [rtype (first s)
	      tag (kword (second s))]
	  (cond (= :node rtype)     (do
				      (push+ (rest t))
				      (recur (conj acc tag (nth s 2))))
		(= :branch rtype)   (recur (conj acc tag (parse2 (rest t))))
		(= :stop rtype)     (do (push+ (rest t))
					acc)
;;		(= :multival rtype) (recur (append-to-last acc (second s))
;;					   (rest text))
		:else               (assert false))))))))

(defn tokenize
  [text]
  "Tokenizes an input string into fields: node value branch multival"
  (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)" text))

(def ncdir   "/Documents and Settings/Administrator/My Documents/Downloads/nc/0")
(def ncfile "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000002768-10-000019.nc")
(def ncfile1 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000015847-10-000026.nc")
(def ncfile2 "/tim/clojure/nctest.txt")
(def ncfilebig "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000067590-10-000736.nc")

(defn run []
  (text-flag-reset)
  (reset-stack)
  (let [x (map line-class (tokenize (read-ncfile ncfilebig)))
	text-only (filter  between-text? x)
	wo-text (filter (complement between-text?) x)
	parsed (parse2 wo-text)]
    (conj parsed text-only)))

(defn parse3
  [file]
  (text-flag-reset)
  (reset-stack)
  (let [x (map line-class (tokenize (read-ncfile file)))
	text-only (filter between-text? x)
	wo-text (filter (complement between-text?) x)
	parsed (parse2 wo-text)]
    [parsed text-only]))

(doseq [file (take 100 (line-seq (clojure.java.io/reader ncdir)))]
  (let [parsed (parse3 file)]
    (println (format "%s - %s"
		     (-> parsed second (nth 2))
;;		     (-> parsed second (nth 15) second second)
		     (count parsed)))))

;; (use 'clojure.inspector)
;; (inspect-tree

(defn print-tree
  [table coll]
  (loop [coll coll]
    (let [f1 (first coll)
	  k (if (keyword? f1) (name f1) f1)
	  v (second coll)]
      (if-not k
	nil
	(do
	  (if (coll? v) (print-tree k v) (push+ [table k]))
	  (recur (rest (rest coll))))))))

(defn print-tree
  [table coll]
  (loop [coll coll]
    (let [[x & xs] coll
	  k (if (keyword? x) (name x) x)
	  v (first xs)]
      (if-not k
	nil
	(do
	  (if (coll? v) (print-tree k v) (push+ [table k]))
	  (recur (rest xs)))))))

(defn print-tree
  [table coll]
  (loop [coll coll]
    (let [[k & xs] coll
	  v (first xs)]
      (if-not k
	nil
	(do
	  (if (coll? v) (print-tree k v) (push+ [table k]))
	  (recur (rest xs)))))))

(defn push++
  [m k v w]
  (assoc m k (conj (k m) [v w])))

(defn print-tree
  [acc table coll]
  (loop [acc acc coll coll]
    (let [[k v & xs] coll]
      (if-not k
	acc
	(recur (if (coll? v)
		 (print-tree acc k v)
		 (push++ acc table k v))
	         xs)))))

(defn rel-model
  [coll]
  (letfn [(save [m k v w]
		(assoc m k (conj (k m) [v w])))
	  (f [acc tab coll]
	     (loop [acc acc coll coll]
	       (let [[k v & xs] coll]
		 (if-not k
		   acc
		   (recur (if (coll? v)
			    (f acc k v)
			    (save acc tab k v))
			  xs)))))]
    (f {} "" coll)))



