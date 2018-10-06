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

```bash
java -Xbootclasspath/a:$JAVA_HOME/lib/tools.jar -cp target\classes se.hirt.examples.svc.attach.ListSystemProperties <pid>
```

A few things worth noting in regards to attach:

* Since JDK 9, you will need to start the JVM with the option
`-Djdk.attach.allowAttachSelf=true` to allow the process to attach to itself. It may seem a ridiculous thing to allow, but for tools it is often the case that discovery of all processes, and retrieval of information used to identifying all those processes (including the process itself), will be done over attach. Not allowing self-attach may lead to special code paths, making the code and its internal APIs harder to maintain.
* When used with a SecurityManager, ensure that the `com.sun.tools.attach.AttachPermission` is granted. The permission target name attachVirtualMachine allows the process to attach to another JVM, createAttachProvider allows the creation of attach providers which allows attaching to other JVMs.