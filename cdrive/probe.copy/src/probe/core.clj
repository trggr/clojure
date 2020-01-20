;;;;
;;;; Displays, side-by-side, the days of a data loads in two environments
;;;;

(ns probe.core
  (:use clojure.set)
  (:use clojure.contrib.sql))

(def query-dev
  "with x as (
      select d.yyyymm, e.process_name, to_char(max(e.version), 'mm/dd/yyyy') ver
      from busintel.etl_batch e, busintel.d_date d
      where e.batch_upto > timestamp_iso(d.month_first_day) and
            e.batch_from < (timestamp_iso(d.month_first_day) + 1 month) and
            e.status = 'OK' and d.year >= 2009 and d.day_of_month = 1
      group by d.yyyymm, e.process_name)
    select x.yyyymm mon, x.process_name process, x.ver
    from x, busintel.data_source d
    where x.process_name = d.data_source_cd
    order by x.yyyymm, d.report_order")

(def query-prod
   "with x as (
      select d.yyyymm, e.process_name, to_char(max(e.version), 'mm/dd/yyyy') ver
      from busintel.etl_batch e, busintel.d_date d
      where e.batch_upto > timestamp_iso(d.month_first_day) and
            e.batch_from < (timestamp_iso(d.month_first_day) + 1 month) and
            e.status = 'OK' and d.year >= 2009 and d.day_of_month = 1
      group by d.yyyymm, e.process_name)
    select x.yyyymm mon, x.process_name process, x.ver ver_prod, d.data_source_desc desc
    from x, busintel.data_source d
    where x.process_name = d.data_source_cd
    order by x.yyyymm, d.report_order")

(def devdb
  {:classname "com.ibm.db2.jcc.DB2Driver"
    :subprotocol "db2"
    :subname "//btscqm02.boulder.ibm.com:50000/cqm_dw:retrieveMessagesFromServerOnGetMessage=true;"
    :user "cqmetl"
    :password "fun2work"})

(def prodb
  {:classname "com.ibm.db2.jcc.DB2Driver"
    :subprotocol "db2"
    :subname "//b03cxnp01103.gho.boulder.ibm.com:60001/cqm_prod"
    :user "cqmetl"
    :password "moh00ple"})

;; Emulates query from two databases:
;; SELECT
;;   pror.mon, pror.process,
;;   pror.ver_prod, devr.ver, prod.desc
;; FROM
;;   pror, devr
;; WHERE
;;   pror.mon = devr.mon AND
;;   pror.process = devr.process
;; ORDER BY
;;   mon, process
;(def devpro (sort-by #(vec (map % [:mon :process]))
;	      (join pror devr {:mon :mon :process :process})))

(defn unite
  "joins two map sets by key pairs"
  [km xcol ycol]
  (vec (join xcol ycol km)))

(defn order-by
  "orders a sequence by given keys"
  [keys coll]
  (let [compare1 (fn [x] (vec (map x [:k :v])))]
    (sort-by compare1 coll)))

(defn run-query
  []
  (def devr (with-connection devdb
	    (with-query-results devr [query-dev] (into [] devr))))
  (def pror (with-connection prodb
	      (with-query-results pror [query-prod] (into [] pror)))))
(run-query1 devdb)

(defn main
  [pror devr]
  (run-query)
  (unite {:mon :mon :process :process} pror devr)
  (doseq [r devpro]
    (printf "%s %17s %s %s\n"
      (:mon r)
      (:process r)
      (:ver_prod r)
      (:ver r))))

(order-by [:k :v]
  (unite {:k :k}
    [ {:k 1 :v "a"}
      {:k 2 :v "b"}
      {:k 3 :v "c"}] [ {:k 1 :u 1}
		       {:k 3 :u 3}]))
