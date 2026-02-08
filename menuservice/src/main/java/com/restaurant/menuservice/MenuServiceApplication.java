package com.restaurant.menuservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDubbo
@EnableKafka
@EnableDiscoveryClient
public class MenuServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenuServiceApplication.class, args);
	}

}
