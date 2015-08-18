# A Simple Python/Clojure Project for Apache Storm

The intent of this project was to discover the smallest possible amount of code/work required to create a Python-based topology that could run successfully on both a local and a remote Storm cluster.  A secondary goal was to accomplish this without writing any Java code, working instead with Clojure, which is supported by Storm out of the box.

### Dependencies
* [Apache Storm](http://storm.apache.org/), "a free and open source distributed realtime computation system."  I used 0.9.2.  Follow [these instructions](http://storm.apache.org/documentation/Setting-up-development-environment.html) - just download, unpack, and add /bin to PATH.
* [Python](http://www.python.org/) 2.x for the Storm topology components.  I used 2.7.5, and the core library is all you need.
* [Leiningen](http://leiningen.org/) utility for managing Clojure projects.  We'll use the `lein` command to kick things off.

### A Nickel Tour of the Code
* __project.clj__  This file describes the project to Leiningen.  When you use `lein`, this file gives Leiningen some basic instructions.
* __/src/clj/__ The Clojure source files are here.  This folder is specified in __project.clj__.
* __stormlocal.clj__ Copied verbatim from v0.0.13 of Parsley's [streamparse](https://github.com/Parsely/streamparse/tree/v0.0.13) project.  This code calls into Storm's Java libraries to spin up a local Storm cluster, pass our topology to the cluster, then (after a short delay) shut down the cluster.  We'll invoke this file to run our topology locally.
* __testremote.clj__ This code calls into Storm's Java libraries to pass our topology to a remote Storm cluster.  We'll invoke this file to run our topology remotely.
* __testtopology.clj__ This code defines our topology (using Clojure to abstract us from the underlying Java libraries).
* __/multilang/resources/__ Our Python files go here.
* __storm.py__  Copied verbatim from v0.9.2 of Apache's [incubator-storm](https://github.com/apache/storm/tree/v0.9.2-incubating-security) example project.  This "helper module" provides the Storm Spout and Bolt base classes that we'll use in our own code.
* __testspout.py__ Our minimalist Storm Spout.  It emits a random letter to the topology stream every five seconds.
* __testbolt.py__ Our minimalist Storm Bolt.  It doubles whatever it receives from the topology stream.  For example, if it receives 'x', it will emit 'xx'.

### Running Locally
No additional setup or configuration is required.  Simply navigate to the project root and run:

`$ lein run -m stormlocal -s ./src/clj/testtopology.clj -t 30000`

`lein run` gets things started.
`-m stormlocal` tells Leiningen in which namespace it can find the `:main` method - the entry point in our project.  The "stormlocal" namespace is declared in our __stormlocal.clj__ file.
`-s ./src/clj/testtopology.clj` is passed into `:main`, and tells the code which topology definition we want it to pass into Storm.
`-t 30000` overrides the default five-second pause before the stormlocal code shuts down the local Storm cluster.  Thirty seconds gives us more time to observe the output, but you could supply any value here.

The console will vomit for several seconds while the various components of the local Storm cluster are spun up and connected, but eventually you should being to see groups of messages - one every five seconds - showing the single letters emitted by the spout, and the double letters being emitted by the bolt, like this:

```
36160 [Thread-17-test-spout] INFO  backtype.storm.daemon.task - Emitting: test-spout default ["e"]
36161 [Thread-15-test-bolt] INFO  backtype.storm.daemon.executor - Processing received message source: test-spout:3, stream: default, id: {}, ["e"]
36161 [Thread-24] INFO  backtype.storm.daemon.task - Emitting: test-bolt default ["ee"]
```

After thirty seconds (or whatever delay you specified), our application will shut down the Storm cluster and exit.

### Running Remotely

Before you can run your topology on a remote Storm cluster, you'll need a remote Storm cluster running.  Use [these instructions](https://github.com/mattskone/storm-python-demo/wiki/Building-a-Storm-VM) to set one up on an Ubuntu VM.

The Storm release you installed locally from the Dependencies section above provides you with the `storm` command line interface, which we'll use to communicate with the remote Storm cluster.  To tell the `storm` CLI where to find the remote cluster, edit the __/conf/storm.yaml__ file in your local Storm installation directory, adding this line (substituting the IP address of the remote Storm cluster):

`nimbus.host: "192.168.33.122"`

_(Note: the [Storm documentation](http://storm.apache.org/documentation/Setting-up-development-environment.html) says to edit **~/.storm/storm.yaml**, but I edited the existing **/conf/storm.yaml** to apparently the same effect.)_

You can test your CLI by running the `list` command, which lists the topologies running on the remote cluster:

`$ storm list`

Since no topologies are running yet, the console output should end with: 

```
857  [main] INFO  backtype.storm.thrift - Connecting to Nimbus at 192.168.33.122:6627
No topologies running.
```

With the CLI working, we're ready to run our Python topology on the remote cluster. This is a two-step process:

1. Compile our Clojure/Python topology project to a jar file.

 From our project root, run this command:

 `$ lein jar`

 Lein will package all of our Clojure and Python source code into a single jar and place it in the __/target/__ directory in our project.

1. Submit our topology jar to the remote cluster.

 Use the `storm` CLI to submit our topology to the remote cluster:

 ```
$ storm jar target/testleinproj-0.0.1-SNAPSHOT.jar testremote "./src/clj/testtopology.clj"
```

 `$ storm jar` is the command to submit a topology to the remote cluster.
 `target/testleinproj-0.0.1-SNAPSHOT.jar` is the name of the jar file from step 1 above.
 `testremote` is the class name to run (our project's entry point).
 `"./src/clj/testtopology.clj"` is the argument to our entry point, specifying the topology definition we want to use.

 If your topology was submitted successfully, the console output should end with:

 ```
2047 [main] INFO  backtype.storm.StormSubmitter - Finished submitting topology: MyPythonTopology
```

 You can also verify that your topology is running by checking the Storm UI here (substituting the correct IP address): http://192.168.33.122:8080

 You can kill your topology from the Storm UI, or with the `storm` CLI:

 `$ storm kill MyPythonTopology`