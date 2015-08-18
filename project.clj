(defproject testleinproj "0.0.1-SNAPSHOT"
    :source-paths ["src/clj"]
    :resource-paths ["multilang"]
    :aot :all
    :min-lein-version "2.0.0"
    :main stormlocal
    :dependencies [[org.apache.storm/storm-core "0.9.2-incubating"]
        [org.clojure/clojure "1.5.1"]
        [org.clojure/data.json "0.2.5"]
        [org.clojure/tools.cli "0.3.1"]
    ]
)