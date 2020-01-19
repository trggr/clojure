(use 'clojure.set)

(sorted-set (union name surname))
(intersection name surname)

;;#{{:name "Tom", :lastname "Benneche", :address 19}
;;  {:name "Joyce", :lastname "Benneche", :address 19}
;;  {:name "Tim", :lastname "Ashchepkov", :address 26, :gender "male"}
;;  {:name "Natalya", :lastname "Ashchepkova", :address 26, :gender "female"}}
(join surname name {:address :address :gender :gender})

;; select a male who lives on at address 26

(select (fn [x] (and (= (:address x) 26)
		     (= (:gender x) "male")))
	(join surname name {:address :address :gender :gender}))


(def people
     #{{:first "Tim"     :last "Ashchepkov"  :dob "17-aug-1969" :gender "male"   :city "Simsbury"}
       {:first "Natalya" :last "Ashchepkova" :dob "8-sep-1969"  :gender "female" :city "Simsbury"}
       {:first "Ana"     :last "Ashchepkova" :dob "29-nov-1998" :gender "female" :city "Simsbury"}
       {:first "Lydia"   :last "Ashchepkova" :dob "14-oct-2000" :gender "female" :city "Simsbury"}
       {:first "Yolka"   :last "Ashchepkova" :dob "6-oct-2007"  :gender "female" :city "Simsbury" :pet "Y"}
       {:first "Nina"    :last "Sorokina"    :dob "3-apr-1971"  :gender "female" :city "Calgary"}
       {:first "Yura"    :last "Sorokin"     :dob "15-may-1965" :gender "male"   :city "Calgary"}
       {:first "Olesya"  :last "Sorokina"    :dob "20-apr-1990" :gender "female" :city "Calgary"}
       {:first "Roma"    :last "Sorokin"     :dob "18-jan-1998" :gender "male"   :city "Calgary"}})

(def cities
     #{{:city "Simsbury" :country "USA"}
       {:city "Calgary" :country "Canada"}
       {:city "Irkutsk" :country "Russia"}
       {:city "Vladivostok" :country "Russia"}})

;; this returns
;;  #{{:last "Ashchepkov", :first "Tim"}
;;    {:last "Ashchepkova", :first "Natalya"}
;;    {:last "Ashchepkova", :first "Ana"}
;;    {:last "Ashchepkova", :first "Lydia"}
;;    {:last "Ashchepkova", :first "Yolka"}}
(project
 (get (index people [:city])
      {:city "Simsbury"})
 [:first :last])

(project
  (get by-city {:city "Simsbury"})
  [:first :last])

(select (fn [x] (= (:country x) "USA")) cities)

(join people cities {:city :city}) [:first])

(get people {:city "Simsbury"})

(seq cities)