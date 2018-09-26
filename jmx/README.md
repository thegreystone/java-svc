# JMX

JMX stands for the Java Management Extensions. It is an API useful for monitoring a JVM. Many useful key statistics can be gathered, such as the heap size.

This simple example connects to a JVM with the specified ServiceURI, and does a linear regression of the heap size after GC, for the last N values gathered, to try to figure out if there is a memory leak.

To run these examples, first do a Maven install in the *root* (java-svc):

```bash
mvn install
```

Then run the examples by running the launchers, for example:

```bash
target/bin/listSystemProperties <pid>
```
