# JPLIS

JPLIS stands for Java Programming Language Instrumentation Services. It provides a Java API for allowing modifications to the byte code of Java classes. 

This simple example allows the wall clock timing of specific method invocations. The methods to instrument are declared in a properties file.

## Building
If you didn't build the parent project, simply run:

```bash
mvn clean package
```

## Running the Example

To run the example without the agent:

```bash
java -cp target/svc-jplis-0.0.1-SNAPSHOT-tests.jar se.hirt.examples.svc.jplis.TestProgram
```

To run the example with the agent:

```bash
java -javaagent:target/svc-jplis-0.0.1-SNAPSHOT.jar -cp target/svc-jplis-0.0.1-SNAPSHOT-tests.jar se.hirt.examples.svc.jplis.TestProgram
```

To configure the agent for another application, copy the default.probes files somewhere, and then set the path to your new probes file as an argument to the agent:

```bash
java -javaagent:target/svc-jplis-0.0.1-SNAPSHOT.jar=<path to your probes file> <rest of your parameters>
```

## Noteworthy
Here are a few things to keep in mind when making instrumentation agents:

* You may start wanting to dynamically create classes on the fly. If so, Unsafe#defineClass and other Unsafe methods can come in handy.

* When starting out using Unsafe, you'll probably wonder how to access it. Well... 

```java
	public static Unsafe getUnsafe() {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");			f.setAccessible(true);
			return (Unsafe) f.get(null);
		} catch (Exception e) {
			...
		}
		return null;
	}
```
* ASM is a slim and powerful bytecode engineering library. It allows you great control over the emitted byte code. That said, there are easier libraries that, for example, allow you to compile Java code. Since this is a rather simple example, I thought it could also be a nice and easily digested introduction to ASM.

* When working with ASM in Eclipse, check out the various disassemblers available. I like Bytecode Outline. If you search for ASM in Eclipse Marketplace, that will probably be your first hit.