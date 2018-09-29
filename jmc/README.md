# JMC
JDK Mission Control is not only the companion tool to JFR, which can be used to analyse flight recordings.
It is also a set of libraries making it easy to analyse recordings programmatically.

## Building
This project requires JDK 7 or later to build and run. The resulting applications can read flight recordings from Oracle JDK 7u40 all the way to Open/Oracle JDK 11 and beyond.

To build, simply run mvn package:

```bash
mvn package
```

## Running

To run the examples, simply run the launchers built under target/bin.

The following example will analyze the provided Flight Recording and produce an HTML report:

```bash
target/bin/htmlReport latency.jfr
```

The following example will print out some statistics for the monitor enter events in the same recording:

```bash
target/bin/monitorEnter latency.jfr
```
