(use '[clojure.string :exclude [replace reverse join]])

(def ncfile "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000002768-10-000019.nc")
(def ncfile1 "/Documents and Settings/Administrator/My Documents/Downloads/nc/0000015847-10-000026.nc")

(defn read-ncfile
  [file]
  "Reads NC file into a string"
  (slurp file))

(defn valuable?
  [val]
  (not (empty? val)))

(defn kword
  [node]
  "Turns node into a keyword"
  (keyword (lower-case node)))

(defn parse2
  [acc clipboard text]
  "Parse text into acc, using a clipboard"
  (println (first text))
  (if (empty? text)
    [acc clipboard text]
    (let [[line node val stop multival]
	  (first text)]
      (print (format "-> %s\t\t: " line))
      (cond
       (and
	node (valuable? val)) (do
				(println (format "node = %s, val = %s" node val))
				(parse2 (conj acc (kword node) val)
					clipboard
					(rest text)))
	node                       (do (println "branch = " node)
				       (let [[subtree
					      clp
					      unprocessed-text] (parse2 acc clipboard (rest text))]
					 (parse2 (conj acc (kword node) subtree)
						 clp
						 unprocessed-text)))
	stop                       (do (println "stop = " stop)
				       [acc clipboard (rest text)])
	multival                   (do (println "multival = " multival)
				       (parse2 acc (conj clipboard multival) (rest text)))
	:else                      (do (println "I shouldn't be here!")
				       (parse2 acc clipboard (rest text)))))))

(defn parse3
  [text]
  "Parses a string into a tree"
  (parse2 []
	  []
	  (re-seq #"(<[A-Z]+.*>)(.*)|(</[A-Z]+.*>)|(.+)"
		  text)))

(parse3 test)

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
<xml>
 <line1>val1</line1>
 <line2>val2</line2>
</xml>
</H>
")


