(ns probe.test.core
  (:use [lazytest.describe :only (describe it)])
  (:use probe.core))

(describe unite
  (it "unites two seqs by key pairs"
    (= [ {:k 1 :v "v1" :u "u1"}
	 {:k 2 :v "v2" :u "u2"}]
      (unite {:k :k}
	[ {:k 1 :v "v1"}
	  {:k 2 :v "v2"}][ {:k 1 :u "u1"}
			   {:k 2 :u "u2"}]))))

(describe unite-2
  (it "unites two seqs by key pairs-2"
    (= [ {:k 1 :v "v1" :u "u1"}
	 {:k 2 :v "v2" :u "u2"}
	 {:k 1 :v "v11" :u "u1"} ]
      (unite {:k :k}
	[ {:k 1 :v "v1"}
	  {:k 2 :v "v2"}
	  {:k 1 :v "v11"}][ {:k 1 :u "u1"}
			    {:k 2 :u "u2"}]))))

(describe order-by
  (it "orders a seq by keys"
    (= [ {:k 1 :v "b"}
	 {:k 1 :v "c"}
	 {:k 3 :v "a"}]
      (order-by [:k :v]
	[ {:k 3 :v "a"}
	  {:k 1 :v "b"}
	  {:k 1 :v "c"}]))))

(describe order-by-2
  (it "orders a seq by keys-2"
    (= [ {:k 1 :v "b" :u 1}
	 {:k 1 :v "c" :u 2}
	 {:k 3 :v "a" :u 3}]
      (order-by [:k :v]
	[ {:k 3 :v "a" :u 3}
	  {:k 1 :v "b" :u 1}
	  {:k 1 :v "c" :u 2}]))))

(describe unite-then-order-by
  (it "unites two seqs, then orders it by keys"
    (= [ {:v "a" :k 1 :u 1}
	 {:v "c" :k 3 :u 3}]
      (order-by [:k :v]
	(unite {:k :k}
	  [ {:k 1 :v "a"}
	    {:k 2 :v "b"}
	    {:k 3 :v "c"}] [ {:k 1 :u 1}
			     {:k 3 :u 3}])))))
