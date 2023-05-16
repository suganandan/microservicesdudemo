package com.du.tools.toolsregistryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ToolsregistryserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToolsregistryserverApplication.class, args);
	}

}
