package com.shantanum.discoveryservernew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerNewApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerNewApplication.class, args);
    }
}