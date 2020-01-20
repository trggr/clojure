set CP=c:/tim/clojure/reddit/lib/clojure-1.2.0.jar
set CP=%CP%;c:/tim/clojure/reddit/lib/clojure-contrib-1.2.0.jar
rem set CP=%CP%;c:/tim/clojure/reddit/lib/commons-codec-1.4.jar       
rem set CP=%CP%;c:/tim/clojure/reddit/lib/commons-fileupload-1.2.1.jar
rem set CP=%CP%;c:/tim/clojure/reddit/lib/commons-io-1.4.jar          
set CP=%CP%;c:/tim/clojure/reddit/lib/compojure-0.6.2.jar
rem set CP=%CP%;c:/tim/clojure/reddit/lib/joda-time-1.6.jar           
rem set CP=%CP%;c:/tim/clojure/reddit/lib/ring-core-0.3.7.jar         
rem set CP=%CP%;c:/tim/clojure/reddit/lib/servlet-api-2.5.jar         
rem set CP=%CP%;c:/tim/clojure/reddit/lib/clout-0.4.1.jar             
set CP=%CP%;.

java -cp %CP% clojure.main %1 %2 %3 %4 %5 %6 %7 %8 %9