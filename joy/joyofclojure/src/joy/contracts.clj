(ns joy.contracts
  "The contracts example from chapter 8.")


(declare collect-bodies)
    
(defmacro defcontract 
  [& forms]
  (let [name (if (symbol? (first forms)) ;; #1_defcontract: Check if name was supplied
               (first forms) 
               nil)
        body (collect-bodies (if name
                               (rest forms) ;; #3_defcontract:  Process rest if so
                               forms))]
    (list* 'fn name body))) ;; #4_defcontract: Build fn form

(declare build-contract)
    

(defn collect-bodies [forms]
  (for [form (partition 3 forms)]
    (build-contract form)))


(defn build-contract [c]
  (let [args (first c)]    ;; #1_build-contract: Grab args
    (list                  ;; #2_build-contract: Build list...
     (into '[f] args)      ;; #3_build-contract: Include HOF + args
     (apply merge
            (for [con (rest c)]            
              (cond (= (first con) 'require) ;; #4_build-contract: Process "requires"
                    (assoc {} :pre (vec (rest con)))
                    (= (first con) 'ensure) ;; #5_build-contract: Process "ensures"
                    (assoc {} :post (vec (rest con)))
                    :else (throw (Exception. (str "Unknown tag " (first con)))))))
     (list* 'f args)))) ;; #6_build-contract: Build call to f
        

(def doubler-contract ;; #1_contract_comp: Define a contract
  (defcontract doubler 
    [x]
    (require
     (pos? x))
    (ensure
     (= (* 2 x) %))))
    
(def times2 (partial doubler-contract #(* 2 %))) ;; #2_contract_comp: Test correct fn
    ;; [compose_contract]: Composition of contract function and constrained function
