(use '(incanter core io))
(def ds (read-dataset
	 "http://gdrd80.watson.ibm.com/cqm/sev12/sev12u-AP-prod-2011-05-01-2011-05-01-ALL.csv"
	 :header true))