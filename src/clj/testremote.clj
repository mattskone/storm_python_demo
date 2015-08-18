(ns testremote
  (:import [backtype.storm StormSubmitter LocalCluster])
  (:use [backtype.storm clojure config])
  (:gen-class))

(defn submit-topology! [topology-file]
    (StormSubmitter/submitTopology
        ;; topology name (arbitrary)
        "MyPythonTopology"
        ;; topology settings
        {TOPOLOGY-DEBUG true}
        ;; topology configuration
        (apply topology (var-get (load-file topology-file)))))

(defn -main
  ;; [topology-file] is relative path to the topology file
  ([topology-file]
   (submit-topology! topology-file)))
