# JDI
JDI (Java Debug Interface) is the Java API used to implement a Java debugger. Different ways to connect are available, but most Java developers probably have experience starting an application in debug mode, setting breakpoints and looking at method parameter values etc. A common and powerful use case is to connect to the JVM remotely using JDWP (the Java Debug Wire Protocol).

This folder contains examples doing exactly that - remotely connecting to a running JVM.

There are various different ways a process can attach in the JPDA (Java Platform Debugger Architecture). In our examples we will be using dt_socket, which is just normal socket communication using the JDWP (Java Debugger Wire Protocol, https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/jdwp-spec.html). See https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/conninv.html for other methods. 

## Building
Note that JDI is contained in the tools.jar, and consequently needs a JDK. The examples will work with JDK 8+. It will be built as part of the other examples if the parent project is built.
To build only this example, do:

```bash
mvn package
```

## Running the examples

First, in one terminal, run the launcher for the example application:

```bash
mvn target/bin/exampleProgram
```

Make note of the port opened in the example application.

In another terminal, run the launcher for one of the other applications, and provide the host and port to connect to, for example:

```bash
mvn target/bin/helloWorld localhost 9898
```

## Noteworthy
There are many nice additions to JDI in JDK 9+ that the examples do not utilize. See the JDI javadocs (https://download.java.net/java/early_access/jdk11/docs/api/jdk.jdi/module-summary.html) to find out what was added.