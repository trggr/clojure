(ns probe.test.parse
  (:use [lazytest.describe :only (describe it)])
  (:use probe.parse))

(defn- self-and-double [x] (list x (+ x x)))

(describe mappend
  (it "apply fs to each element of list and append the results"
    (= '(1 2 10 20 300 600)
      (mappend self-and-double '(1 10 300)))))

(describe parse
  (it "assembles a program"
    (= 2
      (parse '( (load 0)
		(inc)
		(inc))))))


