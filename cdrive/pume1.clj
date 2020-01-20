;; Find top 10 most frequently used words in a file
(use 'clojure.contrib.str-utils)
(use 'clojure.set)
(use '[clojure.string :exclude [replace reverse join]])

(def srcfiles
     {:core-ag   "http://gdrd80.watson.ibm.com/cqm/prism-report/prism-report-AG.log"
      :core-ap   "http://gdrd80.watson.ibm.com/cqm/prism-report/prism-report-AP.log"
      :core-emea "http://gdrd80.watson.ibm.com/cqm/prism-report/prism-report-EMEA.log"
      :ume-ag    "http://gdrd80.watson.ibm.com/cqm/ume-report/ume-report-AG.log"
      :ume-ap    "http://gdrd80.watson.ibm.com/cqm/ume-report/ume-report-AP.log"
      :ume-emea  "http://gdrd80.watson.ibm.com/cqm/ume-report/ume-report-EMEA.log"})
      
;; INC CDIR-0000010364 2  'K'LINE
;;     2011:   0 0 0 | 0 0 0 | 0 0 0 | 0 1 0 |
;;     2010:   0 0 0 | 0 0 0 | 0 0 0 | 0 0 0 |
;;     2009:   0 0 0 | 0 0 0 | 0 0 0 | 0 0 0 |

(defrecord increc [type cdir sev comp mon])

(defmacro redir
  "Redirects output to file"
  [f & body]
  `(spit ~f (with-out-str ~@body)))

(defn toint
  "Parses a string which represents an integer"
  [s]
  (try
    (. Integer parseInt s)
    (catch Exception e 0)))
    
(defn parse-into-paragraphs
  "Parses a string into INC or CHG paragraphs"
  [s]
  (re-split #"\n\n" s))

(defn parse-1-year
  "Parse (2011:   0 1 2 0 0 0 0 0 0 0 0 0)"
  [year]
  (let [y (chop (first year))]
    (loop [acc {} i 12 m (rest year)]
      (if m
	(recur
	 (assoc acc (keyword (format "%s%02d" y i)) (toint (first m)))
	 (dec i) (next m))
	acc))))

(defn parse-years
  "Parses strings:
      2011:   0 1 2 0 0 0 0 0 0 0 0 0
      2010:   4 0 0 0 0 0 0 0 0 0 0 0
   into: {:201112 0 :201111 1 :201110 2 ... :201101 0 :201012 4 ...}"
  [years]
  (loop [acc {}
	 y (map parse-1-year years)]
    (if y
      (recur (union (first y) acc)
	     (next y))
      acc)))

(defn parse-inc
  "Parses INC entry"
  [inc-entry]
  (let [lines (map #(re-split #"\s+" (trim %))
		   (re-split #"\n" inc-entry))
	line1 (first lines)
	type  (first line1)
	cdir (second line1)
	sev (if (= type "INC")
	      (nth line1 2)
	      ;;     2010:   0 0 0 | 0 0 0 | 0 3 0 | 0 1 1 |
	      ;;     2009:   0 0 0 | 0 0 0 | 0 0 0 | 0 0 0 |

	      nil)
	comp (trim (reduce #(str %1 " " %2)
			   (rest (rest(if (= type "INC")
					(rest line1)
					line1)))))
	months (parse-years (rest lines))]
    (increc. type cdir sev comp months)))

(defn parse-log
  "Parses a log file"
  [fname]
  (->> fname
       slurp
       (re-gsub #" \|" "")
       parse-into-paragraphs
       rest
       (map parse-inc)))

;; (parse-inc
;;  "INC CDIR-0000010364 3  'K'LINE
;;   2011:   0 0 0 0 0 0 0 0 0 0 0 0
;;   2010:   0 0 0 0 0 0 0 3 0 0 1 1
;;   2009:   0 0 0 0 0 0 0 0 0 0 0 0")

;; (parse-inc
;;  "CHG CDIR-0000010364 'K'LINE
;;   2011:   0 0 0 0 0 0 0 0 0 0 0 0
;;   2010:   0 0 0 0 0 0 0 3 0 0 1 1
;;   2009:   0 0 0 0 0 0 0 0 0 0 0 0")

(defn prr
  "Concatenates elements of coll and prints it on a separate line"
  [node]
  (reduce #(str %1 "," %2)
	  (->> x :mon sort vals)))

;; (defn pr3
;;   "Prints values of hashmap"
;;   ([node]
;;      (pr3 (keys node) node))
;;   ([ks node]
;;      (let [f (apply juxt ks)]
;;        (reduce #(apply println (f %2))
;; 	       () (first node)))))
(defn pr3
  "Prints values of hashmap"
  ([node]
     (pr3 (keys node) node))
  ([ks node]
     (doseq [k ks]
       (print (k node) " "))
     (println)))

;; (defn print-all
;;   "Prints all values of a seq"
;;   ([mp]
;;      (print-all (keys (into {} (val (first mp))))
;; 		mp))
;;   ([ks mp]
;;      (doseq [k (keys mp)]
;;        (pr3 ks (first (get mp k))))))

(defn print-set
  "Prints all values of a seq"
  ([mp]
     (print-all (keys (into {} (val (first mp))))
		mp))
  ([ks mp]
     (doseq [k (keys mp)]
       (doseq [v (get mp k)]
	 (pr3 ks v)))))

(defn missing
  "Missing in UME"
  [ume core]
  (let [ks [:cdir :sev :type]
	iume (index ume ks)
	icore (index core ks)]
    (reduce (fn [result item]
	      (if (contains? iume (key item))
		(dissoc result (key item))
		result))
		icore icore)))

(defn read-files
  "Reads the data from the source files"
  [src]
  (def core-ag (parse-log (:core-ag src)))
  (def core-ap (parse-log (:core-ap src)))
  (def core-emea (parse-log (:core-emea src)))

  (def ume-ag (parse-log (:ume-ag src)))
  (def ume-ap (parse-log (:ume-ap src)))
  (def ume-emea (parse-log (:ume-emea src))))

(defn filter-map
  "Returns a sub-map of mp where each element satisfies pred"
  [pred mp]
  (reduce (fn [result x] (if (pred x)
			   result
			   (dissoc result (key x))))
	  mp mp))

(defn miss
  [ume core]
  (let [f #(contains? #{"1" "2" nil} (:sev (key %)))
	t (filter-map f (missing ume core))]
    (index (flatten (map #(into '() %) (vals t))) [:cdir])))

;; (defn main
;;   []
;;   (let [f #(contains? #{"1" "2" nil} (:sev (key %)))
;; 	mag (filter-map f (missing ume-ag core-ag))
;; 	map (filter-map f (missing ume-ap core-ap))
;; 	eap (filter-map f (missing ume-emea core-emea))]
;;     (redir "/tim/logs/ag.txt" (print-all [:comp :cdir :sev] mag))
;;     (redir "/tim/logs/ap.txt" (print-all [:comp :cdir :sev] map))
;;     (redir "/tim/logs/emea.txt" (print-all [:comp :cdir :sev] eap))))

(defn main
  []
  (let [mag (miss ume-ag core-ag)
	map (miss ume-ap core-ap)
	eap (miss ume-emea core-emea)]
    (redir "/tim/logs/ag.txt" (print-set [:comp :cdir :sev] mag))
    (redir "/tim/logs/ap.txt" (print-set [:comp :cdir :sev] map))
    (redir "/tim/logs/emea.txt" (print-set [:comp :cdir :sev] eap))))

(doseq [k (keys xx)]
  (into '() (get xx k)))

(loop [ret '() kk (keys xx)]
  (if (first kk)
    (recur (conj ret (get xx (first kk)))
	   (rest kk)))
  ret)
