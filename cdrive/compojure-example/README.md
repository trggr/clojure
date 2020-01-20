This is an example web application that uses [Ring][1] and
[Compojure][2]. It demonstrates basic routing and embedded resources.

To play around with this example project, you will first need
[Leiningen][3] installed.

Download the project dependencies with:

    lein deps

Now you can start a development web server with:

    lein ring server

The server will automatically pickup modified source files.
    

lein repl
ser=> (require 'compojure.example.routes)
nil
user=> (use 'ring.util.serve)
java.io.FileNotFoundException: Could not locate ring/util/serve__init.class or r
ing/util/serve.clj on classpath:  (NO_SOURCE_FILE:0)user=>
