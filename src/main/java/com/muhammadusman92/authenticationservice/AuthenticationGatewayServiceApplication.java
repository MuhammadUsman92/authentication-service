package com.muhammadusman92.authenticationservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class AuthenticationGatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationGatewayServiceApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}




}
