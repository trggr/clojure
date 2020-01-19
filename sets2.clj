(use 'clojure.set)

;;;;;;----------------------------------
;; fred and ricky are keys of a record
(defstruct desilu :fred :ricky)

;; create several records
(def x (map
	(fn [n] (struct-map desilu :fred n :ricky 2 :lucy 3 :ethel 4))
	(range 10)))

(def id (accessor desilu :fred))
; can also run (def id (accessor desilu :ricky))

(reduce + (map (fn [y] (id y)) x))


(reduce + [0 1 2 3 4 5 6 7 8 9])
(reduce + [2 2 2 2 2 2 2 2 2 2])