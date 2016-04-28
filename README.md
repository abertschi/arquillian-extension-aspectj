[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.abertschi.arquillian/arquillian-extension-aspectj/badge.svg?style=flat)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22arquillian-extension-aspectj%22)
[![Build Status](https://travis-ci.org/abertschi/arquillian-extension-aspectj.svg?branch=master)](https://travis-ci.org/abertschi/arquillian-extension-aspectj) 


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
For *WebArchive* or *EnterpriseArchive* deployments, the weaving library should be selected by name using: 

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

To speed up the compile-time compilation process, a caching feature can be activated.

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
        <org.apache.maven.offline>true</org.apache.maven.offline>
      </systemPropertyVariables>
    </configuration>
</plugin>
```

## Build it!
This project is compatible with JDK 1.7 but uses features (notablly lambdas) of Java 8.
The [retrolambda-maven-plugin](https://github.com/orfjackal/retrolambda) is used to transpile new features to Java 7.
Set the maven property `JAVA_HOME_1_7` to JAVA_HOME of Java 7.

## Bleeding Edge

Get snapshot artifacts from:
```xml
<repositories>
    <repository>
        <id>abertschi.snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>abertschi.releases</id>
        <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
</repositories>
```
## License
MIT

