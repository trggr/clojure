(map ns-publics (map ns-name (all-ns)))

(map ns-name (all-ns))
;; makes a list
;;  clojure.set
;;  clojure.print
;;  clojure.core

(map #(cons (ns-name %) (ns-publics (ns-name %))) (all-ns))

;;{clojure.set
;;   {rename-keys union select ...}
;;{clojure.pprint
;;     {pprint pp ...}


(defstruct drill :kfn :vfn)
(struct drill (fn [] (map ns-name (all-ns))) all-ns)

(def clojure-api
  (list
    (struct drill
      (fn [] (map ns-name (all-ns)))
      all-ns)
    (struct drill
      (fn [x] (keys (ns-publics x)))
      (fn [x] (vals (ns-publics x))))))
    

(defn drill-down [node]
  (zipmap (:kfn node) (:vfn node)))

(def path
  (list
    (fn [] (zipmap (map ns-name (all-ns)) (all-ns)))  ;; produces level 1
    (fn [x] (ns-publics (ns-name x))) ;; fn to drill-down to level 2
    meta                              ;; drills down to level 3
    ))

(def lev1 ((nth path 0)))
(def lev2 (map (nth path 1)(vals lev1)))
(def lev3 (map #(map (nth path 2) (vals %)) lev2))

(use 'clojure.inspector)
(inspect-tree lev1)
(inspect-tree lev2)
(inspect-tree lev3)

(defn add-kvs
  "adds to hash new key-value pairs from keys and vals"
  [keys vals]
  (loop [k (first keys) v (first vals) acc ()]
    (if (nil? k)
      acc
      (recur (cons k v) (rest keys) (rest vals)))))
    