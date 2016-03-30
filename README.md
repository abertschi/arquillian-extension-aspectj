- wip, no production ready build

# arquillian-extension-aspectj

> A JBoss Arquillian extension for AspectJ.

This extension compile-time weaves (CTW) classes in your arquillian deployment.
    
## Usage

Add arquillian-extension-aspectj in scope test to your maven project.

    <groupId>ch.abertschi.arquillian</groupId>
    <artifactId>arquillian-extension-aspectj</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>test</scope>
    
Add your configuration file to *META-INF/aspectj.json* of your deployment.

## API

Generate a configuration file *aspectj.json* and add it as a manifest resource to your deployment.

```java

String json = AspectJDescriptor
        .create()
        .weavingLibrary("webarchive.war")
        .include("/WEB-INF/classes")
        .exclude("/WEB-INF/classes/ch/abertschi/debug")
        .add()
        .weavingLibrary("webarchive.war/**/jar-to-weave-*.jar")
        .include("/ch/abertschi")
        .exclude("**test")
        .add()
        .aspectLibrary("ch.abertschi:mytest")
        .exclude(CompilerOption.class)
        .add()
        .aspectLibrary("webarchive.war")
        .include("/WEB-INF/classes/ch/abertschi/myaspects")
        .add()
        .exportAsString();

// more options here https://eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html#configuring-load-time-weaving-with-aopxml-files
// more options here http://www.mojohaus.org/aspectj-maven-plugin/compile-mojo.html

```

```json
{
  "weaving" : [ {
    "name" : "webarchive.war",
    "includes" : [ "/WEB-INF/classes" ],
    "excludes" : [ "/WEB-INF/classes/ch/abertschi/debug" ]
  }, {
    "name" : "webarchive.war/**/jar-to-weave-*.jar",
    "includes" : [ "/ch/abertschi" ],
    "excludes" : [ "**test" ]
  } ],
  "aspects" : [ {
    "name" : "ch.abertschi:mytest",
    "includes" : [ ],
    "excludes" : [ "ch.abertschi.arquillian.descriptor.AspectjDescriptorBuilder.CompilerOption" ]
  }, {
    "name" : "webarchive.war",
    "includes" : [ "/WEB-INF/classes/ch/abertschi/myaspects" ],
    "excludes" : [ ]
  } ],
  "compiler" : {
    "verbose" : false
  }
}
```

