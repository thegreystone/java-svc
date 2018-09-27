# java-svc
This repository contains a set of small examples that can be used to demonstrate various popular Java serviceability technologies. The examples are focused on making it easy to getting going with the various serviceability technologies. 

Note that there are already technology demonstrators for most technologies among the standard java demos. The demos in this repository, however, are focusing on making it easier to get started. The examples are easy to build and run, and they are easily digested. Everyone should dare experimenting with these, even relatively inexperienced developers. Not to mention that I needed examples that fit on a slide for a talk. ;)

## Prerequisites
All projects can build with JDK11, and most will build on JDK 8 as well.

You will also need to have Maven 3.5.3+ installed.

## Building
To build all the projects in one go, ensure that you are using JDK 11, and simply run:

```bash
mvn package
```

The projects can also be built individually by entering the subprojects. Some projects may require a `mvn install` of a dependent project to be built in such a manner.

## Running the Projects
Check the README.md files in the subfolders for instructions on how to run the examples.
