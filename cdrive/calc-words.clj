(def symb [\o \i \e \h \s \g \l])
(def symn (conj symb ""))

;; (def permutations
;;      (concat
;;       (for [a symb] (str a))
;;       (for [a symb b symb] (str a b))
;;       (for [a symb b symb c symb] (str a b c))
;;       (for [a symb b symb c symb d symb] (str a b c d))
;;       (for [a symb b symb c symb d symb e symb] (str a b c d e))
;;       (for [a symb b symb c symb d symb e symb f symb] (str a b c d e f))
;;       (for [a symb b symb c symb d symb e symb f symb g symb] (str a b c d e f g))
;;       (for [a symb b symb c symb d symb e symb f symb g symb h symb] (str a b c d e f g h))))

(def permutations
     (for [a symb b symb c symb d symb e symb f symb g symb h symb] (str a b c d e f g h)))

(def words (apply hash-set (re-seq #"\S+" (slurp "/apps/words/words"))))

(reduce (fn [acc x] (if (words x) (conj acc x) acc)) [] permutations)


;;;
(count
 (take 20000000
       (for [a symb b symb c symb d symb e symb f symb g symb h symb] (str a b c d e f g h))))

(reduce (fn [acc x] (if (words x) (conj acc x) acc)) []
        (for [a symn b symb c symb d symb e symb f symb g symb h symb] (str a b c d e f g h)))
