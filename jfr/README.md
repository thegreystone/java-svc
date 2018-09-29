# JFR

The JDK Flight Recorder is a powerful tool for production time profiling and diagnostics. It is a recording engine that allows you to recording information into the runtime itself, at very little overhead.  

It is analogous to the Data Flight Recorder of commercial aircraft - it records what is going on in the runtime, and the application running in the runtime. When something goes bad, the data can be dumped and analysed. 

Information recorded include method profiling, allocation, latency, garbage collection, compilation related data and much more.

The data is recorded in a compact binary format, with very little overhead.

## Building
This project requires JDK 11 or later to build. For information on how to write equivalent Oracle JDK 7 and 8 compatible code, see older articles at hirt.se/blog.

To build, simply run mvn package:

```bash
mvn package
```

## Running

To run the examples, simply run the launchers built under target/bin.

### Programmatic Control of the Recorder
Normally recordings are initiated and dumped using JDK Mission Control and/or JCMD. That said sometimes, for examples with these demos, it is nice to know that the recorder can be controlled programatically.

The HelloJfr example will emit a HelloWorld event every second. Simply run it and connect to it with JFR, or use JCMD to practice controlling JFR from the command line. This is how to start it:

```bash
target/bin/helloJfr
```

The example RecordOnly will calculate 50 Fibonacci numbers itertively and then emit the recorded data to a file specified by the user.

Here is an example on how to run it:

```bash
target/bin/recordOnly ./myrecording.jfr
```

And (with the recording created in the example above), run:

```bash
target/bin/parserExample myrecording.jfr
```

The following example creates and subsequently parses a recording:

```bash
target/bin/parserExample myrecording.jfr
```

For an example that creates and parses a recording in one go, run:

```bash
target/bin/recordAndConsume rcrecording.jfr
```

The following example is great to hook up a JMC to. It keeps calculating the first 50 Fibonacci numbers over and over in two separate threads, one iteratively and one recursively. To run it:

```bash
target/bin/runFibonacci
```

Next hook up to the process with JMC. Create a 2 minute profiling recording. When the recording is done, open the Threads page, and add a new Thread Activity lane for the Fibonacci event. Sort the threads in the Threads table on the Fibonacci group and select the two threads in that group. Enable the thread activity lane you created. Sweet, huh? ;)


## Noteworthy

A few things worth noting in regards to jfr:

* Because of the great performance, it is tempting to use it as a logger, and log every single piece of crap conceivable into it. Resist the temptation! Or at least default to not enabling all of it by default. Some people only run with the in-memory buffers (without a file repo), and they will be less than thrilled to have their effective recording history dwindle to nothing. 

* The parser included with the JDK is only guaranteed to work with recordings from the same JDK. It is also only guaranteed to be able to execute on the JDK it was delivered with. For a parser that works across all JDKs (JDK7+), and that can be executed across all JDKs (JDK7+) see the JMC core libraries. It is available on Maven central, and examples can be found in the jmc folder.