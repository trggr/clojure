(comment


  
  USE reduce INSTEAD OF map WHEN YOU WANT TO MAINTAIN STATE!

                                                -- Tim Ashchepkov
  
  )
;; Map with maintained state
;; Adds numbers, until reaches 15
;; returns:
;; (1 3 6 10 15 15 15 15 15 15)
(map
 (let [v (ref 0)
       put (fn [x] (dosync (ref-set v x)))]
   (fn [x]
     (if (< @v 15) (put (+ @v x)) @v)))
 [1 2 3 4 5 6 7 8 9 10])

;; But instead of map, and without using closures,
;; use reduce to maintain state and walk the collection
;; Outputs text between <text> and </text>
;; e.g. "<text>ing</text><text>pre</text>"

(defn text-only
  [coll]
  (let [f (fn [acc x]
	    (let [[f1 f2] acc]
	      (cond
	       (= "<text>" x) [true (conj f2 x)]
	       (= "</text>" x) [false (conj f2 x)]
	       f1 [f1 (conj f2 x)]
	       :else acc)))]
    (second (reduce f [false []] coll))))

(text-only ["a" "b" "<text>" "i" "n" "g" "</text>" "4" "5" "<text>" "p" "r" "e" "</text>"])

(reduce
 (fn [acc y]
   (let [[f1 f2] acc]
     (cond
      (= "<text>" y) [true (conj f2 y)]
      (= "</text>" y) [false (conj f2 y)]
      f1 [f1 (conj f2 y)]
      :else acc)))
 [false []]
 ["a" "b" "<text>" "i" "n" "g" "</text>" "4" "5" "<text>" "p" "r" "e" "</text>"])
