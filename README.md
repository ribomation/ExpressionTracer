ExpressionTracer
===============

A CA-WIly Introscope tracer-type that can evaluate an [OGNL](http://commons.apache.org/ognl/index.html) invocation
expression on the instrumented object or any of its method arguments or return value.

The idea is to provide a convenient way to retrieve business related metric values, such as the number of
task jobs, generated reports, bets and more.

This Java library is an agent-side plug-in for CA-Wily Introscope. Introscope is a commercial tool for
application performance management (APM) of (large) Java applications in production. In order to use
and/or compile this library you need to have a valid Introscope license. For compilation, a valid Agent.jar
file is required as well. You will need to manually install this JAR file into your local Maven cache,
using the instructions in the POM file of this project. This library has been developed and tested using Introscope version 8

The current version is 1.4

