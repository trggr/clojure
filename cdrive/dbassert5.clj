(ns dbassert)

(use '[clojure.string :exclude [reverse join] :as str])
(use ['clojure.contrib.sql :as 'sql])
(use 'clojure.contrib.str-utils)

;; the text file with queries
(def *queries* "/tim/clojure/dbassert-queries.txt")

;; enables debug output
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
     (vec
      (map :x (query
	       "select company_name || ' (' || cdir_cd || ')' as x
                from busintel.d_company where boarded = 'Y' order by company_id"))))

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

(defmacro redir
  "Redirects output to file"
  [f & body]
  `(spit ~f (with-out-str ~@body)))

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

(defrecord rule [id title fun param])

(defn pretty-print
  "Prints results nicely"
  [coll]
  (println)
  (dotimes [i (count coll)]
    (let [r (nth coll i)
	  rc (first r)]
      (when (not= 0 rc)
	(print "  " (nth companies i) "    ")
	(println (doall (map float (subvec r 1))))))))

;; (print (format "  %.20s  %s\n" (nth companies i)
;; 	       (doall (map float (subvec r 1)))))))))

;; (println (format "  %s:  %s" (nth companies i)
;; 			 (str (map float (subvec r 1)))))))))

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
  "Validates rules for given months"
  [rules months]
  (doseq [month months]
    (println "\n M O N T H   " month "\n")
    (doseq [rule rules]
      (print (format "\n Rule %d: %s" (:id rule) (:title rule)))
      (let [v ((:fun rule) month (:param rule))]
;;	(println v)
	(if (some #{1} (map first v))
	  (pretty-print v)
	  (println " ..................... OK"))))))

(defn val-1
  "Compares :x and :y vectors, using :pred. Returns a list of truth (L-O-T)"
  [month mp]
  (map (:pred mp)((:x mp) month) ((:y mp) month)))

(defn val-2
  "Returns a L-O-T when a company has null value in this month
   yet three previos months have non-nil values"
  [month mp]
  (let [f (:x mp)
	m (f month)
	m-1 (-> month prev f)
	m-2 (-> month prev prev f)
	m-3 (-> month prev prev prev f)]
    (map (fn [x a b c] (vector (if (and (nil? x)
					(not (nil? a))
					(not (nil? b))
					(not (nil? c))) 1 0) a b c))
	 m m-1 m-2 m-3)))

(defn val-3
  "Returns a L-O-T if a company has null value in this and
   two previous months"
  [month mp]
  (let [f (:x mp)
	m (f month)
	m-1 (-> month prev f)
	m-2 (-> month prev prev f)]
;;    (println "m=" m)
;;    (println m-1)
;;    (println m-2)
    (map (fn [a b c]
	   (vector (if (and (= 0.00M a) (= 0.00M b) (= 0.00M c)) 1 0) a b c))
	 m m-1 m-2)))

(load-queries *queries*)

(def rules
     (list (rule. 1 "Sev-1 summary/details [summ det]" val-1 {:pred nvl-dbl- :x as-sev1 :y ad-sev1})
	   (rule. 2 "Sev-2 summary/details [summ det]" val-1 {:pred nvl-dbl- :x as-sev2 :y ad-sev2})
	   (rule. 3 "Sev-1 ticket counts [summ det]" val-1 {:pred nvl-int- :x ca-sev1 :y cd-sev1})
	   (rule. 4 "Sev-2 ticket counts [summ det]" val-1 {:pred nvl-int- :x ca-sev2 :y cd-sev2})
	   (rule. 5 "CSR summary/details [summ det]" val-1 {:pred nvl-dbl- :x as-csr  :y ad-csr})
	   (rule. 6 "CSR ticket counts [summ det]" val-1 {:pred nvl-int- :x cs-csr  :y cd-csr})
;;	   (rule. 7 "Manual Sev-1 MTRS"  val-1 {:pred nvl-dbl- :x ms-sev1 :y md-sev1})
;;	   (rule. 8 "Manual Sev-2 MTRS"  val-1 {:pred nvl-dbl- :x ms-sev2 :y md-sev2})
	   (rule. 9  "No Sev-1 after 3 good months [mon-1 mon-2 mon-3]" val-2 {:x as-sev1})
	   ;; sev2 = null && sev2[m-1] != null && sev2[m-2] != null && sev2[m-3] != null
	   (rule. 10 "No Sev-2 after 3 good months [mon-1 mon-2 mon-3]" val-2 {:x as-sev2})
	   (rule. 11 "No CSR after 3 good months [mon-1 mon-2 mon-3]" val-2 {:x as-csr})
	   (rule. 12 "Sev-1 consistently null or zero" val-3 {:x ase-sev1})
	   (rule. 13 "Sev-2 consistently null or zero" val-3 {:x ase-sev2})
	   (rule. 14 "CSR consistently null or zero" val-3 {:x ase-csr})
	   (rule. 15 "Unix images peak utilization consistently null or zero" val-3 {:x ase-unixpu})
	   (rule. 16 "Intel images peak utilization consistently null or zero" val-3 {:x ase-winpu})
	   (rule. 17 "Security remediations on time consistently null or zero" val-3 {:x ase-srot})
	   (rule. 18 "SAS70 recommendations on time consistently null or zero" val-3 {:x ase-sas70})))

;;(def months ["2010-10-01" "2010-11-01" "2010-12-01" "2011-01-01" "2011-02-01" "2011-03-01"])

(def months ["2011-03-01" "2011-04-01"])

(validate rules months)

;;(dotimes [i (count months)]
;;  (future
;;   (redir (str "/tmp/dbassert4-" (nth months i) ".txt")
;;	  (validate rules (vector (nth months i))))))


