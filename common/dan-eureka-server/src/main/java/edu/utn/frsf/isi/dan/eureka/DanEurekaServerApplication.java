package edu.utn.frsf.isi.dan.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DanEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DanEurekaServerApplication.class, args);
    }
}