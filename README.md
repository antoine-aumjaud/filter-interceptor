filter-interceptor 
==================

**The aim of this API is to provide a solution to fix quickly a production problem without any downtime**.

*filter-interceptor* is a small API witch could introduce a **proxy between a service and a client** of this service.
This proxy search at each call of a service method if a Filter exists and, if it is the case, calls the method of the Filter, 
otherwise call the method of the service.

This Filter could:
* modify **method parameters** before call the real service, 
* modify **treatment** himself (do not call the real service),
* modify **returned values** to the client of the service.

A Filter is a service implementation (i.e: implements the same interface or extends the service class). 
The overrided method(s) must be flagged with an annotation. Such, refactoring on service are visible immediately on Filters.

Filters have been search when the application start in the classpath and in all JAR files presents in a specified folder.
Filters can be reload from this folder. So ***you can add new Filters on runtime !***

Only one Filter can run and override a service method, each Filter has a priority and the Filter with the highest on this method is used.

Filters could be managed by API or by JMX beans. So it is possible to desactivate/reactivate a Filter, change its priority, 
or search and load new Filters in the specified directory on runtime.


---

Example
-------

### Set the proxy
**1/ Create the filter service** used to manage FIlters on services
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
This create a Java Proxy. You can use other integration methods like: Spring AOP, CGLIB or WebServices Filters

**3/ (optional) Initialize JMX**
~~~~java 
FilterServiceMBeansRegister register = new FilterServiceMBeansRegister(filterService);
register.initMBean();
~~~~

Do not forget to unregister MBeans when the application stop by the following command:
~~~~java 
register.resetMBean();
~~~~

### Usage
After the two first steps, you can call your service method as usual:
~~~~java 
sProxy.test1();
~~~~
As no Filter exists, the proxy will call the real service 's'.

### Create a Filter
1/ To create a Filter you must extend 'Filter' generic class and implement 3 methods
~~~~java 
public class Test1Filter extends Filter<IService> {

	public Test1Filter() {
		super("Service ServiceImpl Test 1", 1);   // Set the name of the filter and its priority
	}

	@Override
	public Class<ServiceImpl> getService() {
		return ServiceImpl.class;                 // Give the class concerned by the filter (can be an implemented interface or one of the mother class of the service)
	}

	@Override
	public IService getFilterServiceImpl(IService service) {
		return new ServiveFilter(service);        // Return the overrided implementation of the service
	}
~~~~

2/ Then, you must implement the overrided implementation of the service:
~~~~java 
	private final static class ServiveFilter implements IService { 
		private final IService service;

		public ServiveFilter(IService service) {
			this.service = service;
		}

		@FilteredMethod
		@Override
		public DtoSample1 test1(DtoSample1 in) { 
			in.setA(1000000);
			return service.test1(in);
		}

		@Override
		public int test0(int in) {
			return 0;
		}
		[...]
~~~~
In this sample, the only method filtered is ~~~~test1~~~. Before the real treatment, the ~~~~setA~~~~ method is called.

3/ The final step is to create a text file named ~~~~org.filterinterceptor.spi.Filter~~~~ 
in folder ~~~~META-INF/services~~~~ of your JAR.
This file must just contains the list of your new Filters present in this JAR.
For this example:
~~~~
org.filterinterceptor.sample.spi.Test1Filter
~~~~

4/ You can now put your JAR in the specified folder and reload Filters. 
=> This new Filter is active \o/ !

### JMX
You could manage filters both by API or JMX if initialize:
* get filters list
* reload new filters
* activate or desactivate filters
* change filters priority 

### Complete example
You can find a **complete example** in the test sources [here](https://github.com/antoine-aumjaud/filter-interceptor/blob/master/src/test/java/org/filterinterceptor/sample/MainFilterTest.java).
And [here](https://github.com/antoine-aumjaud/filter-interceptor/blob/master/src/test/resources/org/filterinterceptor/proxy/spring/spring-config.xml) a **Spring** sample.

---

What's coming
-------------
* Add solutions to integrate the Filters like *HessianFilter* or *CXFFilter*.

---

Technology used
---------------

### Main API
* **Java 6**                    : compiled with JDK 6, and run on a JRE 6
* **SPI**                       : (Service Provider Interface) to search automatically Filters classes in classloader
* **SLF 4J**                    : interface to log information, but let the choice to client application of the implementation used (like log4J, LOGBack, JCL, JUL...)

### Integration
* **Java Proxy**                : for basic integration
* **Spring AOP**                : for integration with Spring project

### Developper tools
* **Maven 3**                   : to manage project build and releases
* **Git**                       : for SCM
* **Eclipse Indigo**            : to write sources and run tests
* **Sonatype Maven repository** : to publish the project
* **PGP**                       : to sign artifacts (classes JAR, javadoc JAR and sources JAR)

### Tests
* **JUnit 4**                   : to create tests
* **EasyMock 3**                : to create real unitary tests
* **LogBack**                   : to log information (native implementation of SLF4J)
* **Spring IOC**                : to test Spring integration

---

Links
-----

### SPI
* **JSE reference**                : http://docs.oracle.com/javase/tutorial/sound/SPI-intro.html
* **A good presentation** (French) : http://thecodersbreakfast.net/index.php?post/2008/12/26/Java-%3A-pr%C3%A9sentation-du-Service-Provider-API

### Annotations 
* **JSE reference**                : http://docs.oracle.com/javase/tutorial/java/javaOO/annotations.html

### Aspect
* **Spring AOP**                   : http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/aop-api.html
* **JSE Proxy**                    : http://docs.oracle.com/javase/1.4.2/docs/api/java/lang/reflect/Proxy.html

### Logs
* **SLF4J**                        : http://www.slf4j.org/
* **LOGBack**                      : http://logback.qos.ch/ 
* **Article on soat.fr** (French)  : http://blog.soat.fr/2010/06/la-mort-de-log4j/
 
 