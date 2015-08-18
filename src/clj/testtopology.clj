(ns testtopology
    (:use [backtype.storm.clojure])
    (:gen-class))

(def test-topology
    [
        ;; spout configuration
        {
            "test-spout" (shell-spout-spec
                ;; the command to run, can be any executable
                ["python" "testspout.py"]
                ;; output specification, what named fields will this spout emit?
                ["letter"])
        }

        ;; bolt configuration
        {
            "test-bolt" (shell-bolt-spec
                {"test-spout" :shuffle}
                ["python" "testbolt.py"]
                ["double-letter"])
        }
    ]
)
