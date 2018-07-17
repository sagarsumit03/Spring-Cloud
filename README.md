
# Spring Cloud:

### Spring cloud Architecture:
![](https://github.com/sagarsumit03/Spring-Cloud/blob/master/Diagram.PNG)

###### Running the Application:
start all the Application with their respective main classes. and use below URLs to check the Servers:

http://localhost:8302 -- Eureka Server
http://localhost:8301 -- Consume-Client
http://localhost:8303 -- Expose-Client

to use the clients from eureka server:

http://localhost:8302/api/client1/**
http://localhost:8302/api/client2/**

to Test Hystrix you can goto below URL:
http://localhost:8302/api/client1/hystrix

to Test Sleuth you can goto below URL:
http://localhost:8302/api/client2/sleuth
------------


##### Gateway Server:
When calling any service from the browser we can't call them by their names, thats internal to the services. And as we grow more instances of services and replicas having different ports, to distribute the services to all the instances of the Service we use a **GATEWAY**.
  
>  A gateway is a single entry point into the system, used to handle requests by routing them to the corresponding service. It can also be used for authentication, monitoring, and more.

What is  Zuul Server?
It's a proxy, gateway, an intermediate layer between the end users and your services.
Eureka server solved the problem of giving names to services instead of hardcoding their IP addresses.
But, still, we may have more than one service (instances) running on different ports. So, Zuul …


    Maps between a prefix path, say /consume-client and a service expose-client. It uses Eureka server to route the requested service.
    It load balances (using Ribbon) between instances of a service running on different ports.
    We can filter requests, add authentication, etc.


##### Service Discovery: Eureka Server


    Eureka Instance should not be confused with Eureka Service. Eureka Service refers to the group of microservice instances registered to the Eureka Server that provide the same service.

> The Eureka server is nothing but a service discovery pattern implementation, where every microservice is registered and a client microservice looks up the Eureka server to get a dependent microservice to get the job done.


Every microservice registers itself in the Eureka server when bootstrapped, generally using the {ServiceId} it registers into the Eureka server, or it can use the hostname or any public IP (if those are fixed). After registering, every 30 seconds, it pings the Eureka server to notify it that the service itself is available. If the Eureka server not getting any pings from a service for a quite long time, this service is unregistered from the Eureka server automatically and the Eureka server notifies the new state of the registry to all other services.

##### Ribbon - Load Balancer:
Ribbon provides software load balancers to communicate with cluster of servers. The load balancers provide the following basic functionalities:
Supply the public DNS name or IP of individual servers to communication client
Rotate among a list of servers according to certain logic.


- **RoundRobinRule**
This rule simply choose servers by round robin. It is often used as the default rule or fallback of more advanced rules.

- *Components of load balancer*
  -- Rule - a logic component to determine which server to return from a list
-- Ping - a component running in background to ensure liveness of servers
- *WeightedResponseTimeRule rule* each server is given a weight according to its average response time, lesser the response time gives lesser the weight. This rule randomly selects a server where the possibility is determined by server’s weight.
And the PingUrl will ping every URL to determine the server's availability.



**Register Service as Client:**
--Registering current Service as Eureka client.
```
eureka.client.registerWithEureka=true
eureka.client.fetchRegistry =true
eureka.client.serviceUrl.defaultZone=http://localhost:8302/eureka/
eureka.instance.hostname=localhost
```

**Add Ribbon Load balancing:**
--enabling ribbon to search for all servers listing below to distribute the call:

```
expose-client.ribbon.eureka.enabled: false
expose-client.ribbon.listOfServers: localhost:8303,localhost:9092,localhost:9999
expose-client.ribbon.ServerListRefreshInterval: 15000
```

**Create Eureka Server and Add Zuul Proxy:**
-- register eureka server and add zuul proxy when prefix is the default server-context and register all clients(Services) and map them with below URLs.

Example: the URL will be:
http://localhost:8302/api/client1/**...

@EnableZuulProxy
@EnableEurekaServer
@SpringBootApplication

```yaml
eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false
    server:
      waitTimeInMsWhenSyncEmpty: 0

zuul:
  #Service will be mapped under the /api URI
  prefix: /api
  routes:
    client1:
      url: http://localhost:8301
    client2:
      url: http://localhost:8303
```


##### Hystirx Circuit Breaker:
When calling chained services any service can fail any time. For Example, we have 3 services A, B, C 
  A ->B and B->C  (A calls B and B calls C, lets say C failed)
We need a callback function to handle the Exception and not chain down to the Application A.
Hystrix is there for the rescue. **Hystrix** is the implementation of Circuit Breaker pattern, which gives a control over latency and failure between distributed services. The motto here is to stop cascading the error.
for this Add the below annotation to the spring Boot main Class:
```java
@EnableEurekaClient
@RestController
@EnableDiscoveryClient
@EnableCircuitBreaker
//@RibbonClient(name = "expose-client", configuration = SayHelloConfiguration.class)
@SpringBootApplication
public class ConsumerClientApplication {
```

Spring looks for any method annotated with the @HystrixCommand annotation, and wraps that method so that Hystrix can monitor it.

Hystrix watches for failures in that method, and if failures occures, Hystrix opens the circuit so that subsequent calls will automatically fail. Therefore, and while the circuit is open, Hystrix redirects calls to the fallback method.

```java
@HystrixCommand(fallbackMethod = "fallback")
	@RequestMapping("/hystrix")
	public String hystrix() {
		return restTemplate.getForObject("http://expose-client/circuit-breaker", String.class);
	}
```

**NOTE:** When you define a fallback method with that annotation the fallback method must match the same parameters of the method where you define the Hystrix Command.

------------


##### Sleuth For Tracing Services:
Lets say there are many services, instances or replicas. In that case when services are interacting and calling each other, it can be quite hard to trace one Service's path.

Sleuth makes it possible to trace the requests by adding unique ids to logs.

A trace id (1st) is used for tracking across the microservices; represents the whole journey of a request across all the microservices, while span id (2nd) is used for tracking within the individual microservice.
To Use Sleuth, use regular loggers in the code, and when the Logs are logged(Or executed) Sleuth willl add special Ids we mentioned above:


    2018-07-17 10:40:26.010  INFO [expose-client,11b669ada16b7fae,11b669ada16b7fae,false] 4004 --- [nio-8303-exec-3] c.e.expose.demo.ExposeClientApplication  : Into ExposeClient Sleuth method ... 

