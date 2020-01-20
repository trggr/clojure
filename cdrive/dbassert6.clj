(ns dbassert)

(use '[clojure.string :exclude [reverse join] :as str])
(use ['clojure.contrib.sql :as 'sql])
(use 'clojure.contrib.str-utils)

;; the text file with queries
(def *queries* "/tim/clojure/dbassert-queries.txt")

;; enables debug output
(def *debug* false)

(def *db* {:classname "com.ibm.db2.jcc.DB2Driver"
	   :subprotocol "db2"
	   :subname "//b03cxnp01103.gho.boulder.ibm.com:60001/cqm_prod:retrieveMessagesFromServerOnGetMessage=true;"
	   :user "cqmetl"
	   :password "moh00ple"})

(defn query
  [q]
  "Queries the database. Uses *db* to connect"
  (when *debug*
    (println "Running: " q))
  (with-connection *db*
    (with-query-results res [q]
      (into [] res))))

(defn cv
  "Takes a SQL-query and generates a memoized fn, which accepts a month
   and returns a vector. The vector contains metrics for all boarded companies.
   The query should contain:
    1. Column x, which will be used to extract values
    2. ORDER BY company_id clause"
  [q]
  (memoize 
    (fn [m]
      (let [q1 (str/replace q ":m" m)
	    v (vec (map #(let [x (:x %)] (if x (double x) nil)) (query q1)))]
	v))))

(defn cvboarded
  "Expands query q to cover the values for all boarded companies in a month"
  [q]
  (cv (str "with c as (select company_id from busintel.d_company where boarded = 'Y'),"
	   " f as ("
	   q
	   ") select c.company_id, f.x "
	   "from c left outer join f "
	   "on c.company_id = f.company_id "
	   "order by c.company_id "
	   "with ur")))

(defmacro redir
  "Redirects output to file"
  [f & body]
  `(spit ~f (with-out-str ~@body)))

(defn introduce
  [funcs]
  "Intern functions into namespace"
  (doseq [k (keys funcs)]
    (when *debug*
      (println "Introducing " (name k)))
    (intern *ns* (symbol (name k)) (k funcs)))
  funcs)

(defn load-queries
  [file]
  "Load queries from the text file. Each paragraph in the file is a keyword
   followed by a query. Returns a map"
  (let [para1 (map #(re-split #"\s+" %)
		   (re-split #"\r\n\r\n" (slurp file)))
	kk (map #(-> % first keyword) para1)
	vv (map (fn [x] (->> x
			     rest
			     (reduce #(str %1 " " %2) "")
			     trim)) para1)]
    (introduce
     (zipmap kk (map cvboarded vv)))))

;;  "Rule components"
(defrecord rule
  [id
   title
   fun
   param])

(defn pretty-print
  [rule coll companies]
  "Prints validation results"
  (println (format "\n Rule %d: %s" (:id rule) (:title rule)))
  (doseq [e (sort-by str/upper-case
		     (filter last (map cons companies coll)))]
    (println (format "  %s   %s"
		     (first e)
		     (butlast (rest e))))))

(defn prev
  [month]
  "For a given month returns a previous month.
   Month is expected in YYYY-MM-DD format"
  (let [[yyyy mm dd] (seq (.split #"-" month))
	y (Integer/valueOf yyyy)
	m (Integer/valueOf mm)]
    (if (= m 1)
      (format "%s-12-01" (dec y))
      (format "%s-%02d-01" yyyy (dec m)))))

(defn mapapp
  [f v]
  "Takes fn f and vector of vectors v. Returns a vector which is
   a result of applying f to corresponding elements from each vector.
   (tmp + [[1 2 3] [4 5 6]]) -> [5 7 9]"
  (apply map f v))

(defn materialize
  [rule month]
  "Materializes a rule for a given month, returns a vector of value vectors"
  (vec (for [p (:param rule)] (p month))))

(defn validate
  [pred material-rule]
  "Returns boolean vector, which is a result of
   applying predicate pred upon a materialized rule"
  (vec (mapapp pred material-rule)))

(defn rule-maker-3-good-months
  [id name f]
  "Combines id, name and data retrieval function f into a
   rule 'No metric after three good months'"
  (rule. id name
	 #(and (nil? %1) %2 %3 %4)
	 [f
	  #(f (prev %))
	  #(f (prev (prev %)))
	  #(f (prev (prev (prev %))))]))

(defn rule-maker-consistent-noz
  [id name f]
  "Combines id, name and data retrieval function f into a
   rule 'Metric is consistently zero or null'"
  (letfn [(noz? [x] (or (nil? x) (zero? x)))]
    (rule. id name
	   #(and (noz? %1)
		 (noz? %2)
		 (noz? %3))
	   [f #(f (prev %)) #(f (prev (prev %)))])))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;;        INITIALIZATION
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def companies
     (vec (map :x
	       (query "select company_name || ' (' || cdir_cd || ')' as x
                       from busintel.d_company where boarded = 'Y' order by company_id"))))

(load-queries *queries*)

(def rules
     (list (rule. 1 "Sev-1 summary/details mismatch" #(not (= %1 %2)) [as-sev1 ad-sev1])
	   (rule. 2 "Sev-2 summary/details mismatch" #(not (= %1 %2)) [as-sev2 ad-sev2])
	   (rule. 3 "Sev-1 ticket counts mismatch"  #(not (= %1 %2)) [ca-sev1 cd-sev1])
	   (rule. 4 "Sev-2 ticket counts mismatch"  #(not (= %1 %2)) [ca-sev2 cd-sev2])
	   (rule. 5 "CSR summary/details mismatch"  #(not (= %1 %2)) [as-csr ad-csr])
	   (rule. 6 "CSR ticket counts mismatch"  #(not (= %1 %2)) [cs-csr cd-csr])
	   (rule-maker-3-good-months 9  "No Sev-1 after 3 good months" as-sev1)
	   (rule-maker-3-good-months 10 "No Sev-2 after 3 good months" as-sev2)
	   (rule-maker-3-good-months 11 "No CSR after 3 good months" as-csr)
           (rule-maker-consistent-noz 12 "Sev-1 consistently null or zero" as-sev1)
           (rule-maker-consistent-noz 13 "Sev-2 consistently null or zero" as-sev2)
           (rule-maker-consistent-noz 14 "CSR consistently null or zero" as-csr)
	   (rule-maker-consistent-noz 15 "Unix images peak utilization consistently null or zero" as-unixpu)
	   (rule-maker-consistent-noz 16 "Intel images peak utilization consistently null or zero" as-winpu)
	   (rule-maker-consistent-noz 17 "Security remediations on time consistently null or zero" as-srot)
	   (rule-maker-consistent-noz 18 "SAS70 recommendations on time consistently null or zero" as-sas70)
	   (rule-maker-consistent-noz 20 "Incidents related to capacity consistently null or zero" as-ccri)
	   ))

(def months ["2011-04-01"])

(defn main
  []
  "Main routine"
  (doseq [month months]
    (println "\n\n\n M O N T H   " month "\n\n\n")
    (doseq [rule rules]
      (let [args (materialize rule month)
	    rc   (validate (:fun rule) args)]
	(when (some identity rc)
	  (pretty-print
	   rule
	   (vec (mapapp #(vec %&) (conj args rc)))
	   companies))))))

(main)
