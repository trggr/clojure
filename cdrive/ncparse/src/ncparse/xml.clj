;;;; -----------------------------------------------------
;;;;
;;;; xml.clj - parses NC file (bady formed XML file)
;;;;
;;;; http://www.sec.gov/answers/form13f.htm
;;;; ftp://ftp.sec.gov/edgar/Feed
;;;; http://www.federalreserve.gov/releases/h15/data.htm
;;;; -----------------------------------------------------
(ns ncparse.xml
  (:use [clojure.string :as s :exclude [replace reverse join]]
        [clojure.contrib.str-utils]
	[clojure.xml :as xml]
	[ncparse.ddl]))

(import [java.io ByteArrayInputStream])

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

(defn anti
  "Returns a closing tag. Given tag <text>, returns </text>"
  [tag]
  (str "</" (subs tag 1)))

(defn between-tag?
  "Returns true, when x is between <TEXT> and </TEXT>"
  [tag x]
  (let [y (first x)]
    (cond (and (= y :branch) (= (second x) tag))
	  (do
	    (text-flag-flip))
	  (and (= y :stop) (= (second x) (anti tag)))
	  (do (text-flag-flip)
	      true)
	  :else (text-flag-curval))))

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


(defn standard-xml
  "Converts paganel record to XML. Closes tags, and purifies original file, so
   it is look more like a standard XML.
   Assumes that kvt has no sections between <TEXT> </TEXT>"
  [paga]
  (reduce (fn [acc x]
	    (let [f (partial str acc "\n")
		  [a b c] x]
	      (cond
	       (= a :node)	           (f b (s/replace (s/replace c "&" "&amp;") "'" "''") "</" (subs b 1))
	       (= b "<PRIVATE-TO-PUBLIC>") (f b "YES" "</PRIVATE-TO-PUBLIC>")
	       (= b "<CONFIRMING-COPY>")   (f b "YES" "</CONFIRMING-COPY>")
	       (= b "<PAPER>")             (f b "YES" "</PAPER>")
	       :else                       (f b))))
	  "" paga))

(defn byte-stream
  [s]
  (ByteArrayInputStream. (.getBytes s "UTF-8")))

(defn ncparse
  [file]
  "Parses NC file"
  (text-flag-reset)
;;  (reset-stack)
  (let [paga (->> file read-ncfile tokenize (map paganel))
	notext (filter #(not (between-tag? "<TEXT>" %)) paga)
	xml (standard-xml notext)
	xml-map (parse (byte-stream xml))]
    xml-map))
