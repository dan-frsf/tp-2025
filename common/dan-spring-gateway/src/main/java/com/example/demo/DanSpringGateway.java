package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class DanSpringGateway {

	public static void main(String[] args) {
		System.out.println("Starting Dan Spring Gateway Application...");
		SpringApplication.run(DanSpringGateway.class, args);
	}

}
