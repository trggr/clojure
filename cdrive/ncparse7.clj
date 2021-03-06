(use '[clojure.string :exclude [replace reverse join]])

(def ncfile "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000002768-10-000019.nc")
(def ncfile1 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000015847-10-000026.nc")

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
  [acc clipboard text]
  "Parse text into acc, using a clipboard"
  (dprintln (first text))
  (if (empty? text)
    [acc clipboard text]
    (let [[line node val stop multival]
	  (first text)]
      (dprintln (format "-> %s\t\t: " line))
      (cond
       (and
	node (valuable? val)) (do
				(dprintln (format "node = %s, val = %s" node val))
				(if (valuable? clipboard)
				  (parse2 (conj
					   (append-to-last acc (str "\n" clipboard))
					   (kword node) val)
					  []
					  (rest text))
				  (parse2 (conj acc (kword node) val)
					  clipboard
					  (rest text))))
	node                       (do (dprintln "branch = " node)
				       (assert (empty? clipboard))
				       (let [[subtree
					      clp
					      unprocessed-text] (parse2 [] clipboard (rest text))]
					 (parse2 (conj acc (kword node) subtree)
						 clp
						 unprocessed-text)))
	stop                       (do (dprintln "stop = " stop)
				       (if (not (empty? clipboard))
					 [clipboard "" (rest text)]
					 [acc "" (rest text)]))
	multival                   (do (dprintln "multival = " multival)
				       (parse2 acc (str clipboard "\n" multival) (rest text)))
	:else                      (do (dprintln "I shouldn't be here!")
				       (parse2 acc clipboard (rest text)))))))

(defn parse3
  [text]
  "Parses a string into a tree"
  (first (parse2 []
		 ""
		 (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)"
			 text))))

(parse3 test)

(parse3 (read-ncfile ncfile1))

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


