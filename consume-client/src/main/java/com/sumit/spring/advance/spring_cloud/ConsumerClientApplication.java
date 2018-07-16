package com.sumit.spring.advance.spring_cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableEurekaClient
@RestController
@EnableDiscoveryClient
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

}
