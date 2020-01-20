; Example of closure in clojure
; (curval) -> 0
; (nextval) -> 1
; (nextval) -> 2
; (reset) -> 0
; (set-v 12) -> error: unable to resolve set-v
; v -> error: unable to resolve v

(let [v (ref 0)
      set-v (fn [x] (dosync (ref-set v x)))]
  (defn curval []
    @v)
  (defn reset []
    (set-v 0))
  (defn nextval []
    (set-v (inc @v))))
