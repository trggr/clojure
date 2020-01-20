;; Compare CQM tickets vs Prism tickets
(ns cvp.core)

(use '[clojure.string :exclude [reverse join] :as str])
(use ['clojure.contrib.sql :as 'sql])
(use 'clojure.contrib.str-utils)
(use 'clojure.set)

;; enables debug output
(def *debug* true)

(def cqm {:classname "com.ibm.db2.jcc.DB2Driver"
          :subprotocol "db2"
          :subname "//b03cxnp46023.gho.boulder.ibm.com:60001/cqm_prod:retrieveMessagesFromServerOnGetMessage=true;"
          :user "tashchep"
          :password "rain2snow"})

(def ume {:classname "com.ibm.db2.jcc.DB2Driver"
          :subprotocol "db2"
          :subname "//b03cxnp01028.gho.boulder.ibm.com:50004/smiwsla:retrieveMessagesFromServerOnGetMessage=true;"
          :user "cqmetl"
          :password "heatt1me"})

(def OUTPUT "c:/tmp/0000.txt")

(def cqm-query
   "select substr(g.incident_id, instr(g.incident_id, '-') + 1) incident_id,
       f.incident_id cqm_incident_id,
       integer(to_char(f.time_opened, 'yyyymmdd')) time_opened,
       integer(to_char(f.time_resolved, 'yyyymmdd')) time_resolved
  from cqmetl.tmp_ibm_global g
       left outer join busintel.f_incident f on g.incident_id = f.incident_id
  with ur")

(def ume-query
  "select trim(t.incident_id) incident_id, t.open_date_id, t.resolved_date_id
   from smiw.t_incident t,
        smiw.d_company c
   where t.company_id = c.company_id and
         c.cdir_cd = 'CDIR-0000001329' and
         t.incident_id in (%s)
    with ur")

(defn query
  "Queries the database"
  [db q]
  (when *debug*
    (println "Running: " q))
  (with-connection db
    (with-query-results res [q]
      (into [] res))))

(defn tocsv
  "Takes a collection and turns it into comma-separated string"
  ([coll] (tocsv coll ","))
  ([coll separator]
     (reduce (fn [acc x]
               (str (when-not (empty? acc) (str acc separator))
                    (when-not (nil? x)
                      (condp = (type x)
                        java.lang.String   (str "'" x "'")
                        java.sql.Timestamp (str "'" x "'")
                        java.lang.Integer (str x)
                        :else x))))
             "" coll)))

(defmacro redir
  "Redirects output to file"
  [f & body]
  `(spit ~f (with-out-str ~@body)))

(defn lookup
  "Looks up records in which key = val"
  [key val coll]
  (filter #(= (key %) val) coll))

;;
;; step 1: Get CQM results
;;
(def cqm-results (query cqm cqm-query))

;;
;; step 2: Filter incidents which are not in CQM
;;
(def missing-in-cqm (map :incident_id
                         (filter #(nil? (:cqm_incident_id %))
                                 cqm-results)))
;;
;; step 3: Get UME details for missing tickets
;;
(def ume-missing-dets (query ume
                             (format ume-query (tocsv missing-in-cqm))))

;;
;; step 4: fill-in details for missing tickets
;;
(def rc (map
         (fn [x]
           (let [y (first (lookup :incident_id
                                  (:incident_id x) ume-missing-dets))]
             [(:incident_id x)
              (:cqm_incident_id x)
              (:time_opened x)
              (:time_resolved x)
              (:open_date_id y)
              (:resolved_date_id y)]))
         cqm-results))

(doseq [[in cqmin to tr od rd] rc]
  (println (format "%s %s %s %s %s %s" in cqmin to tr od rd)))

(println (str "Output redirected to... " OUTPUT))

(redir OUTPUT
 (doseq [r rc]
   (println (tocsv r))))
