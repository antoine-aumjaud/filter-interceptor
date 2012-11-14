filter-interceptor 
==================

**The aim of this API is to provide a solution to fix sharply a production problem without any downtime**

*filter-interceptor* is a small API witch could introduce a **proxy between a service and a client** of this service.
This proxy search at each call of a service method if a Filters exists and, if it is the case, calls the method Filter, 
otherwise call the method of the service.

This Filter could:
* modify **method parameters** before call the real service, 
* modify **treatment** himself (do not call the real service),
* modify **returned values** to the client of the service.

Filters have been search on the application start in the classpath and in all JAR files presents in a specified folder.
Filters can been reload from this folder. So ***you can add new Filters on runtime !***
Only one Filter can run and override a service method, each Filter has a priority and the Filter with the highest on this method is used.

A Filter is a service implementation (i.e: implements the same interface or extends the service class). 
The overrided method(s) must be flag with an annotation. Such, refactoring on service are visible immediately on Filters.

Filters could be managed by API or by JMX beans. So it is possible to desactivate/reactivate a Filter, change its priority, 
or search and load new Filters in the specified directory on runtime.


---

## Example

**1/ Create the filter service**
~~~~java 
//Create a filter service
FilterService filterService = new FilterService("./src/test/resources/others_filters");
//Load filters
filterService.initFilters();
~~~~

**2/ Create a filtered service**
~~~~java 
// Create proxy service 
// 's' is the real service
ServiceProxyFactory serviceProxyFactory = new ServiceProxyFactory(filterService);
IService sProxy = serviceProxyFactory.createProxy(s, false);
~~~~
 This create a Java Proxy, you can use other integration methods like: Spring AOP, CGLIB or WebServices Filters

**3/ (optional) Initialize JMX**
~~~~java 
FilterServiceMBeansRegister register = new FilterServiceMBeansRegister(filterService);
register.initMBean();
~~~~

Do not forget at the application stop to unregister MBeans by following command:
~~~~java 
register.resetMBean();
~~~~

After this you can call your service method
~~~~java 
	s.test1();
~~~~

CREATE A FILTER
JAR

You could manage filters too by API or JMX:
* get filters list
* reload new filters
* activate or desactivate a filter
* change the filter priority 

You can find a complete sample in the test sources [here](https://github.com/antoine-aumjaud/filter-interceptor/blob/master/src/test/java/org/filterinterceptor/sample/MainFilterTest.java)

---

## What's coming
* Add more solution to integrate the Filters like *HessianFilter* or *CXFFilter*.

---

## Technology used

### Main API: 
* **Java 6**                    : compiled with JDK 6, and run on a JRE 6
* **SPI**                       : (Service Provider Interface) to search automatically Filters classes in classloader
* **SLF 4J**                    : interface to log information, but let the choice to client application of the implementation used (like log4J, LogBack, JCL, JUL...)

### Integration: 
* **Java Proxy**                : for basic integration
* **Spring AOP**                : for integration with Spring project

### Developper tools: 
* **Maven 3**                   : to manage project build and releases
* **Git**                       : for SCM
* **Eclipse Indigo**            : to write sources and run tests
* **Sonatype Maven repository** : to publish the project
* **PGP**                       : to sign artifacts (classes JAR, Javadoc JAR and sources JAR)

### Tests:
* **JUnit 4**                   : to create tests
* **EasyMock 3**                : to create real unitary tests
* **LogBack**                   : to log informations (native implementation of SLF4J)
* **Spring IOC**                : to test Spring integration

---

## Links

### SPI
* **JSE reference**             : http://docs.oracle.com/javase/tutorial/sound/SPI-intro.html
* **A very good post**          : http://thecodersbreakfast.net/index.php?post/2008/12/26/Java-%3A-pr%C3%A9sentation-du-Service-Provider-API

### Annotations 
* **JSE reference**             : http://docs.oracle.com/javase/tutorial/java/javaOO/annotations.html

### Aspect
* **Spring AOP**                : http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/aop-api.html
* **JSE Proxy**                 : http://docs.oracle.com/javase/1.4.2/docs/api/java/lang/reflect/Proxy.html

### Logs
* **SLF4J**                     : http://www.slf4j.org/
* **LOGBack**                   : http://logback.qos.ch/ - http://blog.soat.fr/2010/06/la-mort-de-log4j/
 
 