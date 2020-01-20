;; Find top 10 most frequently used words in a file
(use 'clojure.contrib.str-utils)
(use 'clojure.set)
(use '[clojure.string :exclude [replace reverse join]])

;; INC CDIR-0000010364 2  'K'LINE
;;     2011:   0 0 0 | 0 0 0 | 0 0 0 | 0 1 0 |
;;     2010:   0 0 0 | 0 0 0 | 0 0 0 | 0 0 0 |
;;     2009:   0 0 0 | 0 0 0 | 0 0 0 | 0 0 0 |

(defstruct inc1 :type :cdir :sev :comp :mon)

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
    (struct inc1 type cdir sev comp months)))

(defn parse-log
  [fname]
  (->> fname
       slurp
       (re-gsub #" \|" "")
       parse-into-paragraphs
       rest
       (map parse-inc)))

(parse-inc
 "INC CDIR-0000010364 3  'K'LINE
  2011:   0 0 0 0 0 0 0 0 0 0 0 0
  2010:   0 0 0 0 0 0 0 3 0 0 1 1
  2009:   0 0 0 0 0 0 0 0 0 0 0 0")

(parse-inc
 "CHG CDIR-0000010364 'K'LINE
  2011:   0 0 0 0 0 0 0 0 0 0 0 0
  2010:   0 0 0 0 0 0 0 3 0 0 1 1
  2009:   0 0 0 0 0 0 0 0 0 0 0 0")

(map (juxt :cdir :sev) (parse-log "/tim/test1.txt"))
(def prism-emea (parse-log "/tim/logs/prism-report-EMEA.log"))
(def ume-ag (parse-log "/tim/logs/ume-report-AG.log"))
(def ume-ap (parse-log "/tim/logs/ume-report-AP.log"))
(def ume-emea (parse-log "/tim/logs/ume-report-EMEA.log"))

(count prism-emea)
;;-> 2437
(count umea-og)
;;-> 3124
(count ume-ap)
;;-> 463
(count ume-emea)
;;-> 2700

(map
 (juxt vals) ;;:cdir :type :sev)
 (filter #(= (:cdir %) "CDIR-0000005467")
	 (join prism-emea
	       (rename-keys ume-emea {:mon :mon1})
	       {:cdir :cdir})))

(map
 (juxt keys) ;;:cdir :type :sev)
 (filter #(= (:cdir %) "CDIR-0000020819")
	 (join prism-emea
	       (rename-keys (into {} ume-emea) {:mon :mon1})
;;	       (rename-keys ume-emea {:mon :mon1})
	       {:type :type
		:cdir :cdir
		:sev :sev})))

(map
 (juxt :cdir :type :sev)
 (filter #(= (:cdir %) "CDIR-0000020879") prism-emea))
(get prism-emea {:cdir "CDIR-0000011466"})

(select #(= (:cdir %) "CDIR-0000001148") ume-ap)

(map #(print (:cdir %)) (take 10 prism-emea))
