package com.sumit.spring.advance.spring_cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@EnableEurekaClient
@RestController
@EnableDiscoveryClient
@EnableCircuitBreaker
//@RibbonClient(name = "expose-client", configuration = SayHelloConfiguration.class)
@SpringBootApplication
public class ConsumerClientApplication {

	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Autowired
	RestTemplate restTemplate;

	public static void main(String[] args) {
		SpringApplication.run(ConsumerClientApplication.class, args);
	}

	@RequestMapping("/greeting")
	public String greeting() {
		return "Hello from EurekaClient!";
	}

	@RequestMapping("/consume")
	public String hello() {
		return restTemplate.getForObject("http://expose-client/hello", String.class);
	}

	@HystrixCommand(fallbackMethod = "fallback")
	@RequestMapping("/hystrix")
	public String hystrix() {
		return restTemplate.getForObject("http://expose-client/circuit-breaker", String.class);
	}

	// a fallback method to be called if failure happened
	public String fallback() {
		return "Circuit Breaker fallback function";
	}

}
