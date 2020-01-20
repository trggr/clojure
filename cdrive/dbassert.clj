(ns dbassert)

(use '[clojure.string :exclude [reverse join] :as str])
(use ['clojure.contrib.sql :as 'sql])

;; (use '[clojure.string :exclude [replace reverse join]])

;; (def db {:classname "com.ibm.db2.jcc.DB2Driver"
;; 	 :subprotocol "db2"
;; 	 :subname "//btscqm02.boulder.ibm.com:50000/cqm_dw:retrieveMessagesFromServerOnGetMessage=true;"
;; 	 :user "cqmetl"
;; 	 :password "fun2work"})

(def db {:classname "com.ibm.db2.jcc.DB2Driver"
	 :subprotocol "db2"
	 :subname "//b03cxnp01103.gho.boulder.ibm.com:60001/cqm_prod:retrieveMessagesFromServerOnGetMessage=true;"
	 :user "cqmetl"
	 :password "moh00ple"})

(def db {:classname "nl.cwi.monetdb.jdbc.MonetDriver"
	 :subprotocol "monetdb"
	 :subname "//127.0.0.1/demo"
	 :user "monetdb"
	 :password "monetdb"})

(defn query
  "Query the database"
  [q]
  (println "Running" q)
  (with-connection db
    (with-query-results res [q]
      (into [] res))))

(def companies
     (map :x (query "select company_id as x from busintel.d_company where boarded = 'Y'")))

;; (defmacro cm
;;   "Takes a SQL-query and generates a memoized fn of two arguments c and m,
;;    which returns a value which a company (c) has in the month (m)"
;;   [name q]
;;   `(def ~name
;; 	(memoize 
;; 	 (fn [c# m#]
;; 	   (let [cid# (if (string? c#) c# (str c#))
;; 		 q1# (str/replace (str/replace ~q ":c" cid#) ":m" m#)
;; 		 v# (first (vals (first (query q1#))))]
;; 	     v#)))))

;; vector version of the query
;;
(query 
"with c as (select company_id from busintel.d_company where boarded = 'Y'),
      f as (select company_id, sum(resolution_hours)/count(1) x
            from busintel.f_incident
            where severity_id = 1 and report_month = '2011-01-01'
            group by company_id)
 select c.company_id, f.x
 from c left outer join f
 on c.company_id = f.company_id
 order by c.company_id")

(cm masev1 "select auto_value x from busintel.f_metric_summary_vw
            where company_id = :c and report_month = ':m' and metric_code = 'SEV1'")
(cm dasev1 "select sum(resolution_hours)/count(1) x from busintel.f_incident f
            where f.severity_id = 1 and f.report_month = ':m' and f.company_id = :c")
(cm masev2 "select auto_value x from busintel.f_metric_summary_vw
            where company_id = :c and report_month = ':m' and metric_code = 'SEV2'")
(cm dasev2 "select sum(resolution_hours)/count(1) x from busintel.f_incident f
            where f.severity_id = 2 and f.report_month = ':m' and f.company_id = :c")
(cm macsr  "select auto_value x from busintel.f_metric_summary_vw
            where company_id = :c and report_month = ':m' and metric_code = 'CSR'")
(cm dacsr  "select decimal(sum(case when success_flag = 'Y' then 1 else 0 end)) / count(1) * 100 x
            from busintel.f_change where company_id = :c and report_month = ':m'")

(defn nvl-
  "Arithmetic minus which treats nils as zeroes"
  [x y]
  (let [x1 (if x x 0M)
	y1 (if y y 0M)]
    (- x1 y1)))

(defrecord rule [id title pred x y])

(def rules
     '((rule. 1 "Auto Sev-1 matches to details" = masev1 dasev1)
       (rule. 2 "Auto Sev-2 matches to details" = masev2 dasev2)
       (rule. 3 "Auto CSR metric matches to details" = macsr dacsr)))

(comment 

  (def x (map #(dasev1 % "2011-01-01") companies))
  (def y (map #(masev1 % "2011-01-01") companies))
  (map nvl- x y)

)

