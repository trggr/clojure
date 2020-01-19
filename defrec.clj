(defrecord acc [cdir name sev1 sev2 csr sev1det sev2det])
(defrecord det [ticket desc workgroup duration])
(def dbs (acc. "CDIR-001" "DBS" 1 2 99.6
	       (det. "IN-001" "System so-o-o-o slow!" "db-supp" 3.05)
	       (det. "IN-002" "What? No UR in Cognos?!" "cog-supp"  2.10)))

(def umedbs (acc. "CDIR-001" "DBS-UME" 2.7 3.1 30.0
		  (det. "IN-002" "What? No UR in Cognos?!" "cog-supp"  2.10)
     		  (det. "MAX-001" "Uber gut!" "cog-supp"  2.10)))

(def prism
     {(acc. "CDIR-001" "DBS" 1 2 99.6
	    (det. "IN-001" "System so-o-o-o slow!" "db-supp" 3.05)
	    (det. "IN-002" "What? No UR in Cognos?!" "cog-supp"  2.10))
      (acc. "CDIR-002" "DBT" 2 4 99.8
	    (det. "INC1" "What's up with RFS metrics?" "" 3.05)
	    (det. "INC2" "More features!" "req-team"  2.10))})

(def ume
     {(acc. "CDIR-001" "DBSU" 1 2 99.7
	   (det. "EME-001" "System so-o-o-o slow!" "db-supp" 3.05)
	   (det. "EME-002" "System too fast. I can't keep up!" "db-supp" 3.05))
      nil})

(count (join ume prism {:dir :cdir}))

(defrecord company [cdir name])

(def prism #{(company. 10 "DBS")
	     (company. 20 "ABB")
	     (company. 30 "IBX")})

(def ume #{(company. 20 "Abb")
	   (company. 10 "Dbs")})

(join ume prism {:cdir :cdir})
;; returns #{#:user.company{:cdir 10, :name "DBS"} #:user.company{:cdir 20, :name "ABB"}}

(count (join ume prism {:cdir :cdir}))
;; returns 2

(defrecord company-prism [cdir name flag])
(defrecord company-ume [cdir name])

(def prism #{(company-prism. 10 "DBS" true)
	     (company-prism. 20 "ABB" false)
	     (company-prism. 30 "IBX" true)})

(def ume #{(company-ume. 20 "Abb")
	   (company-ume. 20 "Abb1")
	   (company-ume. 10 "Dbs")})

(join prism ume {:cdir :cdir})
;;  #{#:user.company-prism{:cdir 10, :name "Dbs", :flag true}
;;    #:user.company-prism{:cdir 20, :name "Abb1", :flag false}
;;    #:user.company-prism{:cdir 20, :name "Abb", :flag false}}

(map (juxt :cdir :name) (join prism ume {:cdir :cdir}))
;; ([10 "Dbs"] [20 "Abb1"] [20 "Abb"])

(count (join prism ume {:cdir :cdir}))
;; returns 2

(map (juxt :cdir :name :nameu)
     (join prism
	   (rename ume {:name :nameu})
	   {:cdir :cdir}))
;; ([20 "ABB" "Abb1"] [20 "ABB" "Abb"] [10 "DBS" "Dbs"])