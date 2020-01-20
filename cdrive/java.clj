(System/getProperties)
(System/getProperty "user.home")


(.freeMemory (Runtime/getRuntime))


(def now (java.util.Date.))
(.toGMTString now)