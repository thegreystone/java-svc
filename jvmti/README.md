# JVMTI

The JVM Tooling Interface is a native interface to the JVM used to build powerful JVM (Java Virtual Machine) tools. It was created by fusing the former JVMDI (JVM Debugger Interface) and JVMPI (JVM Profiling Interface) interfaces. Every Java developer who has used a debugger has used JVMTI (normally through JDP/JDWP).

I initially thought I'd add a simple agent here, but didn't for the following reasons:

* Since you would be compiling a native library, the example will require some additional setup
* Much of what you'd want to do using JVMTI could be accomplished either using JDI, or another serviceability technology
* Since it would be a bit more involved to get started, you might as well go with one of the example agents out there

If someone would really like me to, I could add an example here.
