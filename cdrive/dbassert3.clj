(ns dbassert)

(use '[clojure.string :exclude [reverse join] :as str])
(use ['clojure.contrib.sql :as 'sql])
(use 'clojure.contrib.str-utils)

;; the text file with queries
(def *queries* "/tim/clojure/dbassert-queries.txt")

;; debug print flag
(def *debug* false)

;; (def db {:classname "com.ibm.db2.jcc.DB2Driver"
;; 	 :subprotocol "db2"
;; 	 :subname "//btscqm02.boulder.ibm.com:50000/cqm_dw:retrieveMessagesFromServerOnGetMessage=true;"
;; 	 :user "cqmetl"
;; 	 :password "fun2work"})
;; (def db {:classname "nl.cwi.monetdb.jdbc.MonetDriver"
;; 	 :subprotocol "monetdb"
;; 	 :subname "//127.0.0.1/demo"
;; 	 :user "monetdb"
;; 	 :password "monetdb"})

(def *db* {:classname "com.ibm.db2.jcc.DB2Driver"
	   :subprotocol "db2"
	   :subname "//b03cxnp01103.gho.boulder.ibm.com:60001/cqm_prod:retrieveMessagesFromServerOnGetMessage=true;"
	   :user "cqmetl"
	   :password "moh00ple"})

(defn query
  "Queries the database. Uses *db* to connect"
  [q]
  (when *debug*
    (println "Running: " q))
  (with-connection *db*
    (with-query-results res [q]
      (into [] res))))

(def companies
     (vec (map :x (query "select company_name as x from busintel.d_company where boarded = 'Y' order by company_id"))))

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
	    v (vec (map :x (query q1)))]
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

(defn nvl-
  "Arithmetic minus which treats nils as zeroes"
  [x y]
  (let [x1 (if x x 0M)
	y1 (if y y 0M)]
    (- x1 y1)))

(defn nvl-int-
  "Arithmetic minus which treats nils as zeroes and rounds result to integer"
  [x y]
  [(int (nvl- x y)) x y])

(defn nvl-dbl-
  "Arithmetic minus which treats nils as zeroes and rounds result to double"
  [x y]
  [(double (nvl- x y)) x y])

(defn nvl-
  "Arithmetic minus which treats nils as zeroes and rounds result to double"
  [x y]
  [(double (nvl- x y)) x y])

(defn introduce
  "Intern functions into namespace"
  [funcs]
  (doseq [k (keys funcs)]
    (when *debug*
      (println "Introducing " (name k)))
    (intern *ns* (symbol (name k)) (k funcs)))
  funcs)

(defn load-queries
  "Load queries from the text file. Each paragraph in the file is a keyword
   followed by a query. Returns a map"
  [file]
  (let [para1 (map #(re-split #"\s+" %)
		   (re-split #"\r\n\r\n" (slurp file)))
	kk (map #(-> % first keyword) para1)
	vv (map (fn [x] (->> x
			     rest
			     (reduce #(str %1 " " %2) "")
			     trim)) para1)]
    (introduce
     (zipmap kk (map cvboarded vv)))))

(defrecord rule [id title pred x y fy])

(defn pretty-print
  "Prints results nicely"
  [coll]
  (dotimes [i (count coll)]
    (if-not (= 0 (first (nth coll i)))
      (println
       (format "  %s:  %s" (nth companies i) (nth coll i))))))

(defn prev
  "Returns a previous month. Expects a date in YYYY-MM-DD format"
  [month]
  (let [[yyyy mm dd] (seq (.split #"-" month))
	y (Integer/valueOf yyyy)
	m (Integer/valueOf mm)]
    (if (= m 1)
      (format "%s-12-01" (dec y))
      (format "%s-%02d-01" yyyy (dec m)))))
    
(defn validate
  "Validates rules"
  [rules months]
  (doseq [month months]
    (println "\n M O N T H   " month "\n")
    (doseq [rule rules]
      (println (format "\n Rule %d: %s\n" (:id rule) (:title rule)))
      (pretty-print
       (vec
	(map (:pred rule)
	     ((:x rule) month)
	     ((:y rule) ((:fy rule) month))))))))


(load-queries *queries*)

(def rules
     (list (rule. 1 "Auto Sev-1 MTRS"           nvl-dbl- as-sev1 ad-sev1 identity)
	   (rule. 2 "Auto Sev-2 MTRS"           nvl-dbl- as-sev2 ad-sev2 identity)
	   (rule. 3 "Auto Sev-1 tickets"        nvl-int- ca-sev1 cd-sev1 identity)
	   (rule. 4 "Auto Sev-2 tickets"        nvl-int- ca-sev2 cd-sev2 identity)
	   (rule. 5 "Auto CSR metric"           nvl-dbl- as-csr  ad-csr  identity)
	   (rule. 6 "Auto CSR tickets"          nvl-int- cs-csr  cd-csr  identity)
	   (rule. 7 "Manual Sev-1 MTRS"         nvl-dbl- ms-sev1 md-sev1 identity)
	   (rule. 8 "Manual Sev-2 MTRS"         nvl-dbl- ms-sev2 md-sev2 identity)
	   (rule. 9 "Sev-1 = 10% of last month" nvl-int- ca-sev1 ca-sev1 prev)))

(def months ["2010-10-01"
 	     "2010-11-01"
 	     "2010-12-01"
 	     "2011-01-01"
 	     "2010-02-01"
 	     "2010-03-01"])

(def months ["2010-10-01"])

(validate rules months)
