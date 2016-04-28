- wip, no production ready build

# arquillian-extension-aspectj

> A JBoss Arquillian extension for AspectJ.

This extension compile-time weaves aspects into your Arquillian deployment using the using the AspectJ compiler ("ajc").
    
## Usage

Add *arquillian-extension-aspectj* to your maven project.

```xml
<dependency>
    <groupId>ch.abertschi.arquillian</groupId>
    <artifactId>arquillian-extension-aspectj</artifactId>
    <version>0.0.1-SNAPSHOT</version>
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
        .filter(Filters.include(**/*controller*"))
        .filter(Filters.include(Login.class, Logout.class"))
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
        Assert.assertEquals(greeter.getGreeting(), expected);
    }
}
```

## Implementation resources
- shrinkwrap-resolver-properties:
  - [all properties](https://books.google.ch/books?id=3S0QAwAAQBAJ&pg=PA35&lpg=PA35&dq=org.apache.maven.user-settings&source=bl&ots=iCQHdu0Y5x&sig=8H4MDbGF3tHN7MtvuzU0W2TYELM&hl=en&sa=X&ved=0ahUKEwi91auB1PLLAhVE_iwKHUZ9A64Q6AEIUzAJ#v=onepage&q=org.apache.maven.user-settings&f=false)
  - [behind proxy](http://stackoverflow.com/questions/6291146/arquillian-shrinkwrap-mavendependencyresolver-behind-proxy)

- aj options:
  - more options here https://eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html#configuring-load-time-weaving-with-aopxml-files
  - more options here http://www.mojohaus.org/aspectj-maven-plugin/compile-mojo.html