# Perf

The Java Perf API allows attaching to a locally running java process and accessing the internal instrumentation buffer maintained by the Java virtual machine. The API also allows for creating instrumentation objects in the JVM executing these API methods.
It also includes method for acquiring a platform specific high resolution clock for time stamp and interval measurement purposes.

This entire API is internal and restricted, so we are dirty little hobbitses for even looking at it. It can change, or be removed, at any time.

To save some time (for me) these examples must be run, and built, on JDK 9+. The examples can be made to build and run on JDK 1.4-JDK 1.8 by replacing `import jdk.internal.perf.Perf` 
with `import sun.misc.Perf`, and updating the build script.

To run the example listing the perf counters:

```bash
target/bin/listCounters <pid>
```

To run the example for the JMX dynamic MBean, run the following and then connect with a JMX console, for example the one in JDK Mission Control.

```bash
target/bin/runCounterMBeanExample
```
