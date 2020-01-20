; The Lisp defined in McCarthy's 1960 paper, translated into CL.
; Assumes only quote, atom, eq, cons, car, cdr, cond.
; Bug reports to lispcode@paulgraham.com.

;;(defun null. (x)
;;  (eq x '()))
;;(defun and. (x y)
;;  (cond (x (cond (y 't) ('t '())))
;;        ('t '())))
;; (defun not+ (x)
;;   (cond (x '())
;;         ('t 't)))
;; (defun append. (x y)
;;   (cond ((null. x) y)
;;         ('t (cons (car x) (append. (cdr x) y)))))
;;(defun list. (x y)
;;  (cons x (cons y '())))
(defun pair. (x y)
  (cond ((and. (null. x) (null. y)) '())
        ((and. (not. (atom x)) (not. (atom y)))
         (cons (list. (car x) (car y))
               (pair. (cdr x) (cdr y))))))



(defn atom+ [x] (not (seq? x)))

(defn null+ [x] (nil? x))

(defn and+ [x y]
  (if x
    (if y true
        false)))

(defn not+ [x] (cond x '() :else true))

(defn append+ [x y]
  (cond (empty? x) y
        :else (cons (first x) (append+ (rest x) y))))

(defn list+ [x y]
  (cons x (cons y '())))

(defn pair+ [x y]
  (cond (and+ (empty? x) (empty? y)) '()
        (and+ (seq? x) (seq? y))
        (cons (list+ (first x) (first y))
               (pair+ (rest x) (rest y)))))

(defn assoc+ [x y]
  (println (format "assoc+: %s %s" x y))
  (if (empty? y)
    nil
    (if (= (ffirst y) x) (second (first y))
        (assoc+ x (next y)))))

(defn eq+ [x y]
  (println (format "eq+: %s %s" x y))
  (= x y))

(defn car+ [x]
  (first x))

(defun eval. (e a)
  (cond
    ((atom e) (assoc. e a))
    ((atom (car e))
     (cond
       ((eq (car e) 'quote) (cadr e))
       ((eq (car e) 'atom)  (atom   (eval. (cadr e) a)))
       ((eq (car e) 'eq)    (eq     (eval. (cadr e) a)
                                    (eval. (caddr e) a)))
       ((eq (car e) 'car)   (car    (eval. (cadr e) a)))
       ((eq (car e) 'cdr)   (cdr    (eval. (cadr e) a)))
       ((eq (car e) 'cons)  (cons   (eval. (cadr e) a)
                                    (eval. (caddr e) a)))
       ((eq (car e) 'cond)  (evcon. (cdr e) a))
       ('t (eval. (cons (assoc. (car e) a)
                        (cdr e))
                  a))))
    ((eq (caar e) 'label)
     (eval. (cons (caddar e) (cdr e))
            (cons (list. (cadar e) (car e)) a)))
    ((eq (caar e) 'lambda)
     (eval. (caddar e)
            (append. (pair. (cadar e) (evlis. (cdr e) a))
                     a)))))


(defn eval+ [e a]
  (println (format "eval+: %s %s" e a))
  (cond
    (atom+ e) (assoc+ e a)
    (atom+ (first e)) (cond
                       (= (first e) 'quote+) (fnext e)
                       (= (first e) 'atom+)  (atom+ (eval+ (fnext e) a))
                       (= (first e) 'eq+)    (eq+   (eval+ (fnext e) a)
                                                    (eval+ (first (nnext e)) a))
                       (= (first e) 'car+)   (car+  (eval+ (fnext e) a))
                          
                       :else (eval+ (cons (assoc+ (first e) a)
                                          (rest e))
                                    a))
    :else 'unsupported))

       ((eq (car e) 'car)   (car    (eval. (cadr e) a)))
       ((eq (car e) 'cdr)   (cdr    (eval. (cadr e) a)))
       ((eq (car e) 'cons)  (cons   (eval. (cadr e) a)
                                    (eval. (caddr e) a)))
       ((eq (car e) 'cond)  (evcon. (cdr e) a))
       ('t (eval. (cons (assoc. (car e) a)
                        (cdr e))
                  a))))
    ((eq (caar e) 'label)
     (eval. (cons (caddar e) (cdr e))
            (cons (list. (cadar e) (car e)) a)))
    ((eq (caar e) 'lambda)
     (eval. (caddar e)
            (append. (pair. (cadar e) (evlis. (cdr e) a))
                     a)))))

(defun evcon. (c a)
  (cond ((eval. (caar c) a)
         (eval. (cadar c) a))
        ('t (evcon. (cdr c) a))))

(defun evlis. (m a)
  (cond ((null. m) '())
        ('t (cons (eval.  (car m) a)
                  (evlis. (cdr m) a)))))
