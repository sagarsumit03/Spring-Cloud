package com.example.expose.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class ExposeClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExposeClientApplication.class, args);
	}
	
	
	
	@GetMapping("/hello")
	public String hello() {
		return "Hello From Exposed Client";
	}
}
