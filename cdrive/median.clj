(ns median
  (:use clojure.test))

(defn median
  "Calculates median value"
  [coll]
  (let [n (count coll)
	s (sort coll)]
    (cond
     (= 0 n) nil
     (= 1 n) (first s)
     (= 2 n) (/ (+ (first s) (second s)) 2)
     (odd? n) (nth s (/ (- n 1) 2))
     :else (/ (+ (nth s (- (/ n 2) 1))
		 (nth s (/ n 2)))
	      2))))

(deftest test-median
  (is (= nil (median [])))
  (is (= 1   (median [1])))
  (is (= 1.5 (median [1 2])))
  (is (= 2   (median [1 2 3])))
  (is (= 2.5 (median [1 2 3 4])))
  (is (= 2.5 (median [4 2 1 3])))
  (is (= 2.5 (median '(1 2 3 4))))
  (is (= 3   (median '(2 3 1 5 4))))
  (is (= 2.8 (median '(2.5 3.1 1.2 5.3))))
  (is (= 3.1 (median '(2.5 3.1 1.2 5.3 4.2)))))

(run-tests 'median)
