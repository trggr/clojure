;;
;; Mother's little helper:
;; Interractively runs Prism and CQM queries
;;
(ns cvp.mlh)

(use '[clojure.string :exclude [reverse join replace] :as str])
(use ['clojure.contrib.sql :as 'sql])
(use 'clojure.contrib.str-utils)
(use 'clojure.set)

;; enables debug output
(def *debug* true)

(def cqm {:classname "com.ibm.db2.jcc.DB2Driver"
          :subprotocol "db2"
          :subname "//b03cxnp46023.gho.boulder.ibm.com:60001/cqm_prod:retrieveMessagesFromServerOnGetMessage=true;"
          :user "tashchep"
          :password "look4spring"})

(def ume {:classname "com.ibm.db2.jcc.DB2Driver"
          :subprotocol "db2"
          :subname "//b03cxnp01028.gho.boulder.ibm.com:50004/smiwsla:retrieveMessagesFromServerOnGetMessage=true;"
          :user "cqmetl"
          :password "autumna1"})

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

(defn run []
  (print "> ")
  (flush)
  (loop [i 0 s (read-line)]
    (cond (= \( (first s)) (println (load-string s))
          (= \q (first s)  
             :else            (println (str "Don't know how to handle " s)))
          (when (and (< i 3) (not= "q" s))
            (print "> ")
            (flush)
            (recur (inc i) (read-line))))))

;; (run)

;; example commands
;; query ume inci IN11203777


