;;;; ---------------------------------
;;;;
;;;; Parses NC file
;;;;
;;;; ---------------------------------
(ns ncparse.db
  (:use [clojure.string :exclude [replace reverse join]]
        [clojure.contrib.str-utils]))

(def db {:classname "org.sqlite.JDBC"
	:subprotocol "sqlite"
	:subname "db/nc.sqlite3"})

(def DEBUG nil)

(defn valuable?
  "Returns true, if val has value"
  [val]
  (not (empty? val)))

(defn kword
  "Turns node into a keyword"
  [node]
  (->> node
       chop
       rest
       (apply str)
       lower-case
       keyword))

(defn dprintln
  "When DEBUG variable is set, prints its args"
  [& args]
  (if DEBUG
    (apply println args)))

(defn read-ncfile
  "Reads file, returns its contents as a string"
  [file]
  (slurp file))

(defn paganel
  "Classifies an input line. Returns Paganel array, in which a first element is
   a keyword classifying the line, the second element is a name of tag,
   and -- for :node class only -- the third element is a value of a node"
  [line]
  (let [[ignore node val stop multival] line]
    (cond (and node (valuable? val)) [:node node val]
	  stop                       [:stop stop]
	  node                       [:branch node]
	  multival                   [:multival (trim multival)]
	  :else                      [:other line])))

(let [TFLAG (ref false)
      text-flag-set (fn [x] (dosync (ref-set TFLAG x)))]
  (defn text-flag-curval
    "Returns the value of TFLAG (which is equal to true, when
     a parsing point is in between <TEXT> and </TEXT> tags)"
    []
    @TFLAG)
  (defn text-flag-reset
    "Resets the TFLAG to false"
    []
    (text-flag-set false))
  (defn text-flag-flip
    "Flips TFLAG to opposite value"
    []
    (text-flag-set (not @TFLAG))))

;;
;; Stack implementation
;;
(let [STACK (ref '())
      set-stack (fn [x] (dosync (ref-set STACK x)))]
  (defn push+ [x]
    (set-stack (conj @STACK x)))
  (defn pop+ []
    (let [x (first @STACK)]
      (set-stack (rest @STACK))
      x))
  (defn get-stack []
    @STACK)
  (defn reset-stack []
    (set-stack '())))

(defn between-text?
  "Returns true, when x is between <TEXT> and </TEXT>"
  [x]
  (let [y (first x)]
    (cond (and (= y :branch) (= (second x) "<TEXT>"))
	  (do
	    (text-flag-flip))
	  (and (= y :stop) (= (second x) "</TEXT>"))
	  (do (text-flag-flip)
	      true)
	  :else (text-flag-curval))))

(defn nvl
  "Returns x, if it's not nil, otherwise returns val"
  [x val]
  (if x x val))

(defn tokenize
  "Tokenizes an input text into a sequence of records
   suitable for paganeling"
  [text]
  (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)" text))

(defn kvtree
  "Takes Paganel type sequence, and parses it into a key/value tree,
   in which each key is a keyword, and each value is either a string,
   or an another kvtree.

   Uses stack, so call reset-stack before each new file!"
  [paganel]
  (push+ paganel)
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
		(= :branch rtype)   (recur (conj acc tag (kvtree (rest t))))
		(= :stop rtype)     (do (push+ (rest t))
					acc)
		:else               (assert false)))))))


(defn rel-model
  [kvt]
  "Takes kvt and converts it into a map, in which keys are relational table names,
   and values are col-val pairs"
  (letfn [(save [m k v w]
		(assoc m k (conj (k m) [v w])))
	  (f [acc tab kvt]
	     (loop [acc acc kvt kvt]
	       (let [[k v & xs] kvt]
		 (if-not k
		   acc
		   (recur (if (coll? v) (f acc k v) (save acc tab k v))
			  xs)))))]
    (f {} "" kvt)))

(defn ncparse
  [file]
  "Parses NC file"
  (text-flag-reset)
  (reset-stack)
  (let [x (->> file read-ncfile tokenize (map paganel))
	to (filter between-text? x)
	rm (->> x (filter (complement between-text?)) kvtree rel-model)]
    [rm to]))


;; (def ncsmall "/tim/clojure/nctest.txt")
(def ncbig "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000067590-10-000736.nc")
;; (def ncdir   "/Documents and Settings/Administrator/My Documents/Downloads/nc/0")

(defn test1
  [ncdir]
  (doseq [file (take 10 (line-seq (clojure.java.io/reader ncdir)))]
    (let [parsed (ncparse file)
	  rm (first parsed)]
      (println (format "%s - %s"
		       (second (first (filter #(= (first %) :conformed-name) (:company-data rm))))
		       (count rm))))))

(defn -main
  [& args]
  (test1 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0"))
