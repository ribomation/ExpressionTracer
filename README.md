Background
==========

ExpressionTracer is a CA-WIly Introscope tracer-type agent extension that can evaluate [OGNL](http://commons.apache.org/ognl/index.html) invocation expressions on an instrumented target object or any of its method arguments or return value.

The idea is to provide a convenient way to retrieve business related metric values, such as the number of task jobs, generated reports, bets and more.

Restrictions
------------

This Java library is an agent-side plug-in for CA-Wily Introscope. Introscope is a commercial tool for application performance management (APM) of (large) Java applications in production. In order to use and/or compile this library you need to have a valid Introscope license. For compilation, a valid Agent.jar file is required as well. You will need to manually install this JAR file into your local Maven cache, using the instructions in the POM file of this project. 

This library has been developed and tested using Introscope version 8


Installation and Configuration
==============================

Overview
--------

1. Build or grab the agent extension JAR file and drop it into the $WILY_AGENT/ext directory
1. Create or amend a PBD file (see below), which defines one or more ExpressionTracer configurations. 
1. Ensure the PBD is part of the agent's PBD set (introscope.autoprobe.directivesFile)
1. Optionally, create an explicit expression file (see below)


Logical Tracer Names
--------------------

Before ExpressionTracer (ET) can be used for instrumentation, at least one configuration (_logical tracer name_) must be created within a PBD file. After that it can be used to define instrumentation actions and metrics, preferably in the same PBD file.

The following snippet defines one logical tracer name (ET), with two generic parameters.

```
SetTracerClassMapping:  ET   com.ribomation.expression_tracer.ExpressionTracer  com.wily.introscope.probebuilder.validate.MetricNameValidator
SetTracerParameter:     ET   metricType     METRIC
SetTracerParameter:     ET   retentionMode  RETAIN
```

The metricType parameter value can be one of 

<table>
<tr> <th>Metric Type</th>     <th>Description</th> </tr>
<tr> <td><code>average</code> </td>       <td>Average aggregated value over an IMP</td> </tr>
<tr> <td><code>sum</code> </td>           <td>The aggregated sum over an IMP</td> </tr>
<tr> <td><code>counter</code> </td>       <td>Number of 'events' in an IMP</td> </tr>
<tr> <td><code>last</code> </td>          <td>The last value reported in an IMP</td> </tr>
<tr> <td><code>text</code> </td>          <td>The last reported text value in an IMP</td> </tr>
</table>

The retentionMode parameter value can one of

<table>
<tr> <th>Retention Mode</th>  <th>Description</th> </tr>
<tr> <td><code>none</code> </td>          <td>No retention in an IMP. I.e. no collected value -> no metric value</td> </tr>
<tr> <td><code>zero</code> </td>          <td>Report the value 0, if no data collected within an IMP</td> </tr>
<tr> <td><code>last</code> </td>          <td>Report the last collected value, if no data collected within an IMP</td> </tr>
</table>

(IMP = Introscope Measurement Period = 15s)

Expression Metric Definitions
-----------------------------
Based on one or more logical tracer names, one can create instrumentation points, i.e. expression metric definitions. Here are some sample definitions.

```
TraceOneMethodWithParametersOfClass:   target.Application  compute    ET_avg     "ExpressionTracer Demo|Metric Type:Average=this.getNumber()"
TraceOneMethodWithParametersOfClass:   target.Application  getNumber  ET_avg     "ExpressionTracer Demo|Misc:Return Value=$0"
TraceOneMethodWithParametersOfClass:   target.Application  compute    ET_avg     "ExpressionTracer Demo|Misc:Parameter Value=$1.size()"
```

The metric tracer directive used must be `TraceOneMethodWithParametersOfClass`, because arguments and return values need to be available.

The metric name (within ") is subdivided into a name part and an expression part separated by an equals sign (`=`). The expression part in turn is subdivided into a target part and an OGNL part, and they are separated by a single dot (`.`).

The target part must be one of

* `this` - the instrumented (target) object itself
* `$0` - the returned value object, from the instrumented method
* `$n` - the n:th argument (parameter) object (i.e. $1, $2, $3, ...), of the instrumented method

The expression is any valid OGNL expression. OGNL has a very intuitive syntax (close to Java's) for common cases. Please, read the [OGNL Language Guide](http://commons.apache.org/ognl/language-guide.html) for more information.

It is possible to leave out the expression part (including the '.'), e.g. simply just `$1`. Depending on the metric type the value will be coerced to a numeric value (longValue) or a text string (toString).


Aggregator Functors
-------------------

In addition to the general OGNL expression capabilities, a few aggregator functor are provided intended to be used in combination with collection aggregation (a.k.a. reduce expressions). They are all available as methods on one injected object named `#_`. Here is an example, that expects the first parameter of an instrumented method to be a collection and computes the sum of all elements. 
 
```
"ExpressionTracer Demo|Misc:Sum of parameter list=$1.( #_SUM(#this) )"
```

The available aggregator functors are

<table>
<tr> <th>Aggregator Functor</th>  <th>Description</th> </tr>
<tr> <td><code>SUM</code> </td>          <td>Sum of the elements. <code>{n, acc -> n + acc}</code> </td> </tr>
<tr> <td><code>PROD</code> </td>          <td>Product of the elements. <code>{n, acc -> n * acc}</code> </td> </tr>
<tr> <td><code>MIN</code> </td>          <td>Minimum of the elements. <code>{n, acc -> n < acc ? n : acc}</code> </td> </tr>
<tr> <td><code>MAX</code> </td>          <td>Maximum of the elements. <code>{n, acc -> n > acc ? n : acc}</code> </td> </tr>
</table>


External Expressions
--------------------

Only trivial expressions can be written as part of the metric name within the PBD. More complex expressions are rejected by the agent's PBD parser. In these cases, the expressions can be externalised into a separate file in the agent directory.

1. Create a regular Java properties file named `ExpressionTracer.properties` in the agent directory.
1. Add the expressions as normal properties, where the property name is its ID and the property value is the complete OGNL expression.
1. Within the PBD, refer to the external expression using '@' and its name.
1. It is also possible to use another file name (and location) by setting the agent profile property `ExpressionTracer.file`

Here is an example expression file (based on a real case)

```
nordea=$0.startsWith(\"NORDEA\")
bankid=$0.startsWith(\"BIDT\")
other=$0.(#issuer = #this, !(#issuer.startsWith(\"NORDEA\")  || #issuer.startsWith(\"BIDT\")))
```

Which is used in the following PBD snippet

```
SetTracerClassMapping:  ExpressionTracer_Counter     com.ribomation.expression_tracer.ExpressionTracer  com.wily.introscope.probebuilder.validate.MetricNameValidator
SetTracerParameter:     ExpressionTracer_Counter     metricType     counter

TraceOneMethodWithParametersOfClass:   se.nordicedge.CertificateParser  getCAType    ExpressionTracer_Counter     "NordicEdge|Cert Issuer:Nordea=@nordea"
TraceOneMethodWithParametersOfClass:   se.nordicedge.CertificateParser  getCAType    ExpressionTracer_Counter     "NordicEdge|Cert Issuer:BankID=@bankid"
TraceOneMethodWithParametersOfClass:   se.nordicedge.CertificateParser  getCAType    ExpressionTracer_Counter     "NordicEdge|Cert Issuer:Other=@other"
```

Building this Agent Extension
=============================

The build tool used is [Gradle](http://www.gradle.org). 

Before running the first Gradle command, the `build.gradle` file must be edited. Change the following property to point to an unpacked Introscope Agent (version 8.x).

```
ext.agentDir = 'C:/Tools/Introscope/Agent-8.2.4'
```

After the latest version of Gradle have been installed, the following commands are of primary interest

How to build the agent extension JAR
------------------------------------

```
 gradle build
```

* The agent-extension JAR will be located in the `./build/libs/` directory. 
* In addition, the distribution ZIP containing the extension JAR and sample PBD, will be located in the `./build/distributions/` directory.

How to run the functional test
------------------------------

```
 gradle functestRun
```

This will build the sample `test.Application`, configure an Introscope agent and run the instrumented program. Based on the agent logs, one can follow the actions of the expression-tracer.

How to remove all generated files
------------------------------------

```
 gradle clean
```
