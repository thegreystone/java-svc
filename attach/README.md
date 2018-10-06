# Attach

The Java attach API allows a local java process retrieve information, and send instructions, to another java process. It is, for example, used by JCMD to send Diagnostic Commands. It can also be used for listing properties and dynamically loading JPLIS agents.

Some of the things that can be done through attach:

* Load a JPLIS agent (-javaagent)
* Load a JVMTI agent (-agentlib/-agentpath)
* Get the system properties
* Start the local management agent

Also, if we're being nasty little hobbitses (and downcasting to HotSpotVirtualMachine):
* Run arbitrary Diagnostic Commands (jcmd)
* Perform heap dumps
* Start the JMX over RMI management agent

To run an example on JDK 9+ simply run one of the launchers, for example:

```bash
target/bin/listSystemProperties <pid>
```

To run an example on a JDK 8, run the class with the tools.jar on the bootclasspath, for example:

```bash
java -Xbootclasspath/a:$JAVA_HOME/lib/tools.jar -cp target\classes se.hirt.examples.svc.attach.ListSystemProperties <pid>
```

A few things worth noting in regards to attach:

* Since JDK 9, you will need to start the JVM with the option
`-Djdk.attach.allowAttachSelf=true` to allow the process to attach to itself. It may seem a ridiculous thing to allow, but for tools it is often the case that discovery of all processes, and retrieval of information used to identifying all those processes (including the process itself), will be done over attach. Not allowing self-attach may lead to special code paths, making the code and its internal APIs harder to maintain.
* When used with a SecurityManager, ensure that the `com.sun.tools.attach.AttachPermission` is granted. The permission target name attachVirtualMachine allows the process to attach to another JVM, createAttachProvider allows the creation of attach providers which allows attaching to other JVMs.