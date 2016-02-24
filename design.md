# Builder to create configuration file

```
Descriptor().weaving()
.within("ch.abertschi", recursive=true) // package or class as string
.within("ch.abertschi.*.test") // package or class as string, wildcards
.within("ch.abertschi:myjar.jar") // jar
.within(MyClass.class.getPackage(), recursive=true) // package of class
.within(MyClass.class) // class
.within(MyClass1.class, MyClass2.class ...) // classes

.exclude("ch.abertschi", recursive=true) // package or class as string
.exclude("ch.abertschi.*.test) // package or class as string, wildcards
.exclude("ch.abertschi:myjar.jar") // jar
.excluce(MyClass.class.getPackage(), recursive=true) // package of class
.excluce(MyClass.class) // class
.excluce(MyClass1.class, MyClass2.class ...) // classes

.aspects()
.aspectJar("ch.abertschi.myaspects:jar)
.aspect(MyAspect.class)
.aspect(MyAspect.class.getPackage)

compiler()
.verbose()
// more options here https://eclipse.org/aspectj/doc/next/devguide/ltw-configuration.html#configuring-load-time-weaving-with-aopxml-files
// more options here http://www.mojohaus.org/aspectj-maven-plugin/compile-mojo.html
```

- go through all archives of arquillian deployment, similar to https://github.com/abertschi/aspectj-archive-maven-plugin 
