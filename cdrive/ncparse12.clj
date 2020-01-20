(use '[clojure.string :exclude [replace reverse join]])

(def ncdir   "/Documents and Settings/Administrator/My Documents/Downloads/nc/0")
(def ncfile  "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000002768-10-000019.nc")
(def ncfile1 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000015847-10-000026.nc")
(def ncfile2 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000019446-10-000036.nc")

(defn read-ncfile
  [file]
  "Reads NC file into a string"
  (slurp file))

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
  (if *debug*
    (apply println more)))

(defn line-class
  [line]
  "Classify a an input line"
  (let [[ignore node val stop multival] line]
    (cond (and node (valuable? val)) :node
	  node                       :branch
	  stop                       :stop
	  multival                   :multival
	  :else                      :other)))

(defn append-to-last
  [a s]
  "Appends string s to the last element of array"
  (conj (vec (butlast a)) (str (last a) s)))

(defn parse2
  ([acc pocket text rip-mode]
     "Parse text into acc, using a pocket"
     (dprintln (first text))
       (loop [a acc p pocket s text r rip-mode]
	 (if (empty? s)
	   [a p s r]
	   (let [[line node val stop multival] (first s)]
	     (dprintln (format "-> %s\t\t: " line))
	     (cond
	      (= node "<TEXT>")        (loop [acc1 [] pocket1 "" text1 (rest s)
					let [[q c t] (parse2 [] "" (rest s) true)]
					 (recur (conj a :text c) "" t false))
	      (= stop "</TEXT>")       [a p (rest text)]
	      rip-mode                 (parse2 acc (str pocket "\n" line) (rest text) rip-mode)
	      (and
	       node (valuable? val))   (if (valuable? pocket)
					 (parse2 (conj
						  (append-to-last acc (str "\n" pocket))
						  (kword node) val)
						 []
						 (rest text))
					 (parse2 (conj acc (kword node) val)
						 pocket
					     (rest text)))
	   node                     (let [[a c t] (parse2 [] pocket (rest text))]
				      (parse2 (conj acc (kword node) a) c t))
	   stop                     (if (not (empty? pocket))
				      [pocket "" (rest text)]
				      [acc "" (rest text)])
	   multival                 (parse2 acc (str pocket "\n" multival) (rest text))
	   :else                    (do (dprintln "I shouldn't be here!")
					(parse2 acc pocket (rest text)))))))
  ([acc pocket text]
     (parse2 acc pocket text false)))

(defn parse3
  [text]
  "Parses a string into a tree"
  (first (parse2 []
		 ""
		 (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)"
			 text))))

(doseq [file (take 10 (line-seq (clojure.java.io/reader ncdir)))]
  (println (format "%s - %s"
		   file
		   (count (parse3 (read-ncfile file))))))


(parse3 (read-ncfile ncfile))
(parse3 test)


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
<xml>
 <line1>val1</line1>
 <line2>val2</line2>
</xml>
</H>
")


