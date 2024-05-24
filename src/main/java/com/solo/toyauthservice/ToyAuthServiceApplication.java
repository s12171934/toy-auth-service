package com.solo.toyauthservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ToyAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToyAuthServiceApplication.class, args);
	}

}
