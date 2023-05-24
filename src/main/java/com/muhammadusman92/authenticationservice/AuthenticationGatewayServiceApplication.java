package com.muhammadusman92.authenticationservice;

import com.muhammadusman92.authenticationservice.config.AppConstants;
import com.muhammadusman92.authenticationservice.entity.Role;
import com.muhammadusman92.authenticationservice.repo.RoleRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

@SpringBootApplication
@EnableEurekaClient
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class AuthenticationGatewayServiceApplication implements CommandLineRunner {
	@Autowired
	private RoleRepo roleRepo;

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationGatewayServiceApplication.class);

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


	@Override
	public void run(String... args) throws Exception {
		try {
			Role adminRole=new Role();
			adminRole.setId(AppConstants.ADMIN_USER);
			adminRole.setName("ROLE_ADMIN");
			Role normalRole=new Role();
			normalRole.setId(AppConstants.NORMAL_USER);
			normalRole.setName("ROLE_NORMAL");
			Role rescueUser=new Role();
			rescueUser.setId(AppConstants.RESCUE_USER);
			rescueUser.setName("RESCUE_USER");
			Role policeUser=new Role();
			policeUser.setId(AppConstants.POLICE_USER);
			policeUser.setName("POLICE_USER");
			Role rescueAdmin=new Role();
			rescueAdmin.setId(AppConstants.RESCUE_ADMIN);
			rescueAdmin.setName("RESCUE_ADMIN");
			Role policeAdmin=new Role();
			policeAdmin.setId(AppConstants.POLICE_ADMIN);
			policeAdmin.setName("POLICE_ADMIN");
			List<Role> list=new LinkedList<>();
			list.add(adminRole);
			list.add(normalRole);
			list.add(rescueUser);
			list.add(rescueAdmin);
			list.add(policeAdmin);
			list.add(policeUser);
			roleRepo.saveAll(list);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
