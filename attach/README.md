# Attach

The Java attach API allows a local java process retrieve information, and send instructions, to another java process. It is, for example, used by JCMD to send Diagnostic Commands. It can also be used for listing properties and dynamically loading JPLIS agents.

To run an example on JDK 9+ simply run one of the launchers, for example:

```bash
target/bin/listSystemProperties <pid>
```

To run an example on a JDK 8, run the class with the tools.jar on the bootclasspath, for example:

```bash
java -Xbootclasspath/a:$JAVA_HOME/lib/tools.jar -cp target\classes se.hirt.examples.svc.attach.ListSystemProperties <pid>
```