package com.example.expose.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class ExposeClientApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExposeClientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ExposeClientApplication.class, args);
		
	}

	@GetMapping("/hello")
	public String hello() {
		return "Hello From Exposed Client";
	}

	@GetMapping("/circuit-breaker")
	public String hystrix() throws Exception {
		throw new Exception("exception for Hystrix");
	}

	@GetMapping("/sleuth")
	public String sleuth() {
		LOGGER.info("Into ExposeClient Sleuth method ... ");
		return "Text from Sleuth";
	}
}
