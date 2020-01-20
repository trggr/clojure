;;
;; Mother's little helper:
;; Interractively runs Prism and CQM queries
;;
(ns cvp.mlh2)

(use '[clojure.string :exclude [reverse join replace] :as str])
(use ['clojure.contrib.sql :as 'sql])
(use 'clojure.contrib.str-utils)
(use 'clojure.set)

;; enables debug output
(def debug true)
(def conn {:ume {:classname "com.ibm.db2.jcc.DB2Driver"
                 :subprotocol "db2"
                 :subname (str "//b03cxnp01028.gho.boulder.ibm.com:50004/smiwsla:"
                               "retrieveMessagesFromServerOnGetMessage=true;")
                 :user "cqmetl"
                 :password "snow6bal"}
           :prod {:classname "com.ibm.db2.jcc.DB2Driver"
                  :subprotocol "db2"
                  :subname (str "//b03cxnp46023.gho.boulder.ibm.com:60001/cqm_prod:"
                               "retrieveMessagesFromServerOnGetMessage=true;")
                  :user "tashchep"
                  :password "look4spring"}
           :dev {:classname "com.ibm.db2.jcc.DB2Driver"
                 :subprotocol "db2"
                 :subname (str "//btscqm02.boulder.ibm.com:50000/cqm_dw:"
                               "retrieveMessagesFromServerOnGetMessage=true;")
                 :user "cqmetl"
                 :password "fun2work"}})

;;(def qrfile "/tim/clojure/cvp/sql/mlh-queries.sql")  ;file with queries
(def qrfile "sql/mlh-queries.sql")  ;file with queries

(defn query
  "Queries the database"
  [db q]
  (when debug
    (println (format "query=%s" q)))
  (with-connection db
    (with-query-results res [q]
      (into [] res))))

(defn ppp
  "Strips keys from a vector of maps to unclutter output of query.
   Can be used as a pipe to data retrieval functions.
   E. g. (-> (dcomp cqm \"ace\") ppp)"
  [res]
  (when-not (empty? res)
    (let [cols (keys (first res))]
      (doseq [r (map (apply juxt cols) res)]
        (println r)))
    (println (format "Total %d rows selected" (count res)))))

(defn read-queries
  "Reads queries from the text file, and returns a map.
   Sample input file:

   name1 select x, y from tab
         where z = :1

   name2 select a, b from c

   Queries are separated by a blank lines; a first word in each
   paragraph is a name of a query"
  [file]
  (let [paragraphs (map #(re-split #"\s+" %)
                        (re-split #"\r\n\r\n" (slurp file)))
	ks (map #(-> % first keyword) paragraphs)
	vs (map (fn [x] (->> x
			     rest
			     (reduce #(str %1 " " %2) "")
			     trim)) paragraphs)]
    (zipmap ks vs)))

(defn find-qrparams
  "Analyzes query, and finds which parameters query uses.
   Each parameter is a colon followed by a
   number, e.g. :1, :2, :3.
    > (find-qrparams \"select x, y from :1 where z = :2\")
    (:1 :2)"
  [qr]
  (set (re-seq #":\d+" qr)))

(defn qrfn
  "Takes a SQL-query and generates fn"
  [qr]
  (fn [db & binds]
    (let [params (find-qrparams qr)
          env (zipmap params binds)
          rtqr (reduce (fn [acc [key val]]
                         (str/replace acc key val))
                       qr
                       env)]
      (query db rtqr))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;;                INIT
;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(println (str "Creating functions from " qrfile))
(def queries (read-queries qrfile))
(doseq [q (keys queries)]
  (intern *ns* (symbol (name q)) (qrfn (q queries))))

(doseq [c (keys conn)]
  (intern *ns* (symbol (name c)) (c conn)))

(def help
  (str "Functions: " (keys queries) "; "
       "Connections: " (keys conn) "; "
       "Type help or try (dcomp cqm \"ace\") or (-> (dcomp cqm \"ace\") ppp)"))


