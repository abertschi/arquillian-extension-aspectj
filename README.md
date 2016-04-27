- wip, no production ready build

# arquillian-extension-aspectj

> A JBoss Arquillian extension for AspectJ.

This extension compile-time weaves aspects into your Arquillian deployment using the using the AspectJ compiler ("ajc").
    
## Usage

Add *arquillian-extension-aspectj* to your maven project.

    <groupId>ch.abertschi.arquillian</groupId>
    <artifactId>arquillian-extension-aspectj</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>test</scope>
    
Generate a configuration file *aspectj.json* and add it as a manifest resource to your deployment.

### Configuration

Use the builder class *ch.abertschi.arquillian.descriptor.AspectjDescriptor* to generate a configuration file.

The simplest configuration looks like following:

```java
String json = AspectjDescriptor
        .create()
        .weave()
        .addWeaveDependency()
        .exportAsString();
```
This will compile-time weave everything in your deployment.

#### Weaving and AspectJ libraries

```java
String json = AspectjDescriptor
        .create()
        .weave("**/lib/weaving-lib.jar)
        .aspectLibrary("ch.abertschi:myaspects:1.0.0")
        .addAspectLibrary()
        .aspectLibrary("**/lib/myaspects.jar")
        .addAspectLibrary()
        .addWeaveDependency()
        .exportAsString();
```

#### Filtering

You can filter your weaving and aspect libraries.

```java
String json = AspectjDescriptor
        .create()
        .weave()
        .filter(Filters.exclude("**/Debugger*"))
        .filter(Filters.include(Logging.class.getPackage()))
        .addWeaveDependency()
        .exportAsString();
```

#### Caching

## Arquillian test

## Implementation resources
- shrinkwrap-resolver-properties:
  - [all properties](https://books.google.ch/books?id=3S0QAwAAQBAJ&pg=PA35&lpg=PA35&dq=org.apache.maven.user-settings&source=bl&ots=iCQHdu0Y5x&sig=8H4MDbGF3tHN7MtvuzU0W2TYELM&hl=en&sa=X&ved=0ahUKEwi91auB1PLLAhVE_iwKHUZ9A64Q6AEIUzAJ#v=onepage&q=org.apache.maven.user-settings&f=false)
  - [behind proxy](http://stackoverflow.com/questions/6291146/arquillian-shrinkwrap-mavendependencyresolver-behind-proxy)

- aj options:
  - more options here https://eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html#configuring-load-time-weaving-with-aopxml-files
  - more options here http://www.mojohaus.org/aspectj-maven-plugin/compile-mojo.html