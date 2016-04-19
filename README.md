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


create().weave("webarchive.war")
        .weave().filter(Filters.INCLUDE, Myclass.class)
                .filter(Filters.EXCLUDE, "**/*test")
                .withAspectLibrary("com.mypackage:mylib:1.1.1-SNAPSHOT")
                .
        .weave().filter().include().include().include().exclude().applyFilter()
        .withAspectLibrary().filter()


// how to use filters
// shrinkwrap: all paths start with /
// **/*test should include /**/*test
//

```java

@Deployment
public static Archive<?> deploy()
{

    String json = AspectJDescriptor
            .create()
            .weave("webarchive.war")
                .include("/WEB-INF/classes")
                .exclude("/WEB-INF/classes/ch/abertschi/debug")
                .withAspects("ch.abertschi:mytest")
                .and("ch.abertschi:mytest2")
                .addAspects()
            .and()
            .weave("webarchive.war/**/jar-to-weave-*.jar")
                .include("/ch/abertschi")
                .exclude("**test")
                .withAspects("ch.abertschi:mytest")
                .exclude(CompilerOption.class)
                .addAspects()
            .and()
            .compiler()
            .verbose()
            .and()
            .exportAsString();
        

   EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "my-ear.ear")
        .addAsManifestResource(new StringAsset(json), "aspectj.json")
       // ...;
       
    return ear;
}

```

```json
{
  "weaving" : [ {
    "name" : "webarchive.war",
    "includes" : [ "/WEB-INF/classes" ],
    "excludes" : [ "/WEB-INF/classes/ch/abertschi/debug" ],
    "aspects" : [ {
      "name" : "ch.abertschi:mytest",
      "includes" : [ ],
      "excludes" : [ ]
    }, {
      "name" : "ch.abertschi:mytest2",
      "includes" : [ ],
      "excludes" : [ ]
    } ]
  }, {
    "name" : "webarchive.war/**/jar-to-weave-*.jar",
    "includes" : [ "/ch/abertschi" ],
    "excludes" : [ "**test" ],
    "aspects" : [ {
      "name" : "ch.abertschi:mytest",
      "includes" : [ ],
      "excludes" : [ "**/ch/abertschi/arquillian/descriptor/AspectjDescriptorBuilder/CompilerOption*" ]
    } ]
  } ],
  "compiler" : {
    "verbose" : true
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