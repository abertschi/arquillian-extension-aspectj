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

AspectjDescriptor().weaving()
.within("ch.abertschi", recursive=true)             // package or class as string
.within("ch.abertschi.*.test")                      // package or class as string, wildcards
.within("ch.abertschi:myjar.jar")                   // jar
.within(MyClass.class.getPackage(), recursive=true) // package of class
.within(MyClass.class)                              // class
.within(MyClass1.class, MyClass2.class ...)         // classes

.exclude("ch.abertschi", recursive=true)            // package or class as string
.exclude("ch.abertschi.*.test")                     // package or class as string, wildcards
.exclude("ch.abertschi:myjar.jar")                  // jar
.excluce(MyClass.class.getPackage(), recursive=true) // package of class
.excluce(MyClass.class)                             // class
.excluce(MyClass1.class, MyClass2.class ...)        // classes

.aspects()
.aspectJar("ch.abertschi.myaspects:jar")
.aspect(MyAspect.class)
.aspect(MyAspect.class.getPackage)

compiler()
.verbose();

// more options here https://eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html#configuring-load-time-weaving-with-aopxml-files
// more options here http://www.mojohaus.org/aspectj-maven-plugin/compile-mojo.html

```

```javascript
{
    weaving: {
        libs: [
            "ch.abertschi:myjar.jar",
            "mytests.jar"
        ]
        within: [
            "ch.abertschi.*",
            "ch.abertschi.*.test",
            "MyClass"
        ],
        without: [ 
            "ch.abertschi.exclude.*"
        ]
    },
    
    aspects: [
                libs: [
                    "ch.abertschi:myjar.jar",
                    "mytests.jar"
                ]
                within: [
                    "ch.abertschi.*",
                    "ch.abertschi.*.test",
                    "MyClass"
                ],
                without: [ 
                    "ch.abertschi.exclude.*"
                ]
    ],
    
    compiler: {
        verbose: true
    }
}
```

- go through all archives of arquillian deployment, similar to https://github.com/abertschi/aspectj-archive-maven-plugin 
