filter-interceptor 
==================

filter-interceptor is a small API witch could introduce a proxy between a service and a client of this service.
This proxy search at each call of a service method if a Filters exists and, if it is the case, calls the method Filter, 
otherwise call the method service.

This Filter could: 
* modify method parameters before call the real service, 
* modify treatment himself (do not call the real service)
* modify returned values to the client of the service.

Filters have been search on the application start in the classpath and in all JAR files presents in a specified folder.
Filters can been reload from this folder. So you can add new Filters on runtime !
Only one Filter can run and override a service method, each Filter has a priority and the Filter with the hightest on this method is used.

Filters could be managed by API or by JMX beans. So it is possible to desactivate/reactivate a Filter, change its priority, 
or research new Filters in the specified directory on runtime.

---

### Technology used ###

#### Main API: #### 
* Java 6                    - compiled with JDK 6, and run on a JRE 6
* SPI                       - (Service Provider Interface) to search automatically Filters classes in classloader
* SLF 4J                    - interface to log information, but let the choice to client application of the implementation used (like log4J, LogBack, JCL, JUL...)

#### Integration: #### 
* Java Proxy                - for basic integration
* Spring AOP                - for integration with Spring project

#### Developper tools: #### 
* Maven 3                   - to manage project build and releases
* Git                       - for SCM
* Eclipse Indigo            - to write sources and run tests
* Sonatype Maven repository - to publish the project
* PGP                       - to sign artifacts (classes JAR, Javadoc JAR and sources JAR)

#### Tests: #### 
* JUnit 4                   - to create tests
* EasyMock 3                - to create real unitary tests
* LogBack                   - to log informations (native implementation of SLF4J)
* Spring IOC                - to test Spring integration

---

### What's coming ### 
* Add more solution to integrate the Filters like HessianFilter or CXFFilter.
