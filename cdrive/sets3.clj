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

;;;-----
(defstruct person :id :name :surname :dob :gender :city)

(def x (struct-map person :id 1 :name "Tim" :surname "Ashchepkov" :dob "17-aug-1969" :gender "male" :city "Simsbury"))
(merge-with  
     
 (def persons
      #{(struct-map person :id 1 :name "Tim" :surname "Ashchepkov" :dob "17-aug-1969" :gender "male" :city "Simsbury")
	(struct-map person :id 2 :name "Natalya" :surname "Ashchepkova" :dob "8-sep-1969" :female "female" :city "Simsbury")})

;       {:first "Ana"     :last "Ashchepkova" :dob "29-nov-1998" :gender "female" :city "Simsbury"}
;       {:first "Lydia"   :last "Ashchepkova" :dob "14-oct-2000" :gender "female" :city "Simsbury"}
;       {:first "Yolka"   :last "Ashchepkova" :dob "6-oct-2007"  :gender "female" :city "Simsbury" :pet "Y"}
;       {:first "Nina"    :last "Sorokina"    :dob "3-apr-1971"  :gender "female" :city "Calgary"}
;       {:first "Yura"    :last "Sorokin"     :dob "15-may-1965" :gender "male"   :city "Calgary"}
;       {:first "Olesya"  :last "Sorokina"    :dob "20-apr-1990" :gender "female" :city "Calgary"}
;       {:first "Roma"    :last "Sorokin"     :dob "18-jan-1998" :gender "male"   :city "Calgary"}})

(def id (accessor persons :id))
; can also run (def id (accessor desilu :ricky))

(reduce + (map (fn [y] (id y)) x))

;; create a new struct type
02	(defstruct s :1 :2)
03	#'user/s
04	 
05	user=> (type s)
06	clojure.lang.PersistentStructMap$Def
07	 
08	;; create an instance of this new struct type
09	(def s1 (struct s "one" "two"))
10	#'user/s1
11	 
12	user=> (println s1)
13	{:1 "one", :2 "two"}
14	 
15	user=> (type s1)
16	clojure.lang.PersistentStructMap
17	 
18	;; create a new struct instance that is based on the previous struct type
19	;; with the option of supplying a new key/value pairs
20	(def s2 (struct-map s :3 "three"))
21	#'user/s2
22	 
23	user=> (println s2)
24	{:1 nil, :2 nil, :3 "three"}
25	 
26	user=> (type s2)
27	clojure.lang.PersistentStructMap