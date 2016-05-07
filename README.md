[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.abertschi.arquillian/arquillian-extension-aspectj/badge.svg?style=flat)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22arquillian-extension-aspectj%22)
[![Build Status](https://travis-ci.org/abertschi/arquillian-extension-aspectj.svg?branch=master)](https://travis-ci.org/abertschi/arquillian-extension-aspectj) 
[![codecov](https://codecov.io/gh/abertschi/arquillian-extension-aspectj/branch/master/graph/badge.svg)](https://codecov.io/gh/abertschi/arquillian-extension-aspectj)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e922682f70a64459927b8b256e6bff86)](https://www.codacy.com/app/abertschi/arquillian-extension-aspectj?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=abertschi/arquillian-extension-aspectj&amp;utm_campaign=Badge_Grade)
[![Apache 2](http://img.shields.io/badge/license-APACHE2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

# arquillian-extension-aspectj

> A JBoss Arquillian extension for AspectJ.

This extension compile-time weaves aspects into your Arquillian deployment using the AspectJ compiler ("ajc").
    
## Usage

Add *arquillian-extension-aspectj* to your maven project.

```xml
<dependency>
    <groupId>ch.abertschi.arquillian</groupId>
    <artifactId>arquillian-extension-aspectj</artifactId>
    <version>VERSION</version>
    <scope>test</scope>
</dependency>    
```    
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
This will compile-time weave everything in your deployment archive using applicable aspects found in the deployment.
For *WebArchive* or *EnterpriseArchive* deployments, you may select weaving libraries by name. 

```java
String json = AspectjDescriptor
        .create()
        .weave("**/lib/name-of-weaving-lib*")
        .addWeaveDependency()
        .exportAsString();
```

Glob patterns applicable for [AntPathMatcher](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/util/AntPathMatcher.html) can be used.

#### AspectJ libraries

Aspect libraries can manually be added to the Arquillian archive or resolved from the Internet with Maven Coordinates.
The [Shrinkwrap Resolvers project](https://github.com/shrinkwrap/resolver) is used in that case.

```java
String json = AspectjDescriptor
        .create()
        .weave("**/lib/weaving-lib.jar")
        .aspectLibrary("ch.abertschi:myaspects:1.0.0")
        .addAspectLibrary()
        .aspectLibrary("**/lib/myaspects.jar")
        .addAspectLibrary()
        .addWeaveDependency()
        .exportAsString();
```

#### Filtering

You can filter weaving and aspect libraries.
Filtering by glob pattern, by package name and by class type is supported.

```java
String json = AspectjDescriptor
        .create()
        .weave()
        .filter(Filters.include("**/*controller*"))
        .filter(Filters.include(Login.class, Logout.class))
        .filter(Filters.exclude(Debbuging.class.getPackage()))
        .addWeaveDependency()
        .exportAsString();
```

```java
String json = AspectjDescriptor
        .create()
        .weave()
        .aspectLibrary("ch.abertschi:myaspects:1.0.0")
        .filter(Filters.exclude(DebuggingAspect.class.getPackage()))
        .addAspectLibrary()
        .addWeaveDependency()
        .exportAsString();
```

#### Caching

To speed up the compile-time compilation process, you may activate caching.

```java
String json = AspectjDescriptor
        .create()
        .weave()
        .useCache()
        .addWeaveDependency()
        .exportAsString();
```
The current cache implementation caches compiled artifacts based on their size.

## Arquillian test
```java

@RunWith(Arquillian.class)
public class JarDeploymentIT
{
    @Inject
    DummyGreeter greeter;

    @Deployment
    public static Archive<?> deploy()
    {
        String json = AspectjDescriptor
                .create()
                .weave()
                .addWeaveDependency()
                .exportAsString();

        return ShrinkWrap.create(JavaArchive.class)
                .addClass(JarDeploymentIT.class)
                .addPackages(true, Greeting.class.getPackage())
                .addAsManifestResource(new StringAsset(json), "aspectj.json")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void test_around_aspect()
    {
        String expected = "arquillian!";
        System.out.println(greeter.getGreeting());
        Assert.assertEquals(greeter.getGreeting(), expected); // will say arquillian! instead of aspect!
    }
}
```

```java
@Aspect
public class SayArquillianAspect
{
    @Around("call(* ch.abertschi.arquillian.domain.Greeting.*(..))")
    public Object doNothing()
    {
        return "arquillian!";
    }
}
```

```java
@Stateless
@Startup
public class Greeting
{
    public String greet()
    {
        return "aspect!";
    }
}
```

```java
@Singleton
@Startup
public class DummyGreeter
{
    @Inject
    Greeting greeting;

    @PostConstruct
    public void sayGreeting()
    {
    }

    public String getGreeting() {
        return greeting.greet();
    }
}
```

## Behind a corporate proxy
This extension uses the Shrinkwrap Resolver Project to resolve necessary dependencies. If you encouter connection problems, you may configure the [shrinkwrap-resolver-maven-plugin](https://github.com/shrinkwrap/resolver) and set the following system properties.

```xml
<plugin>
  <groupId>org.jboss.shrinkwrap.resolver</groupId>
  <artifactId>shrinkwrap-resolver-maven-plugin</artifactId>
  <executions>
    <execution>
      <goals>
        <goal>propagate-execution-context</goal>
      </goals>
    </execution>
  </executions>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
      <systemPropertyVariables>
        <shrinkwrap.resolve-via-plugin>true</shrinkwrap.resolve-via-plugin>
      </systemPropertyVariables>
    </configuration>
</plugin>
```

## Bleeding Edge
Get snapshot artifacts from [https://oss.sonatype.org/content/repositories/snapshots](https://oss.sonatype.org/content/repositories/snapshots/ch/abertschi/arquillian/arquillian-extension-aspectj/)