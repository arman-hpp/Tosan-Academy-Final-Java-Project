package com.tosan.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.tosan.*"})
@EnableJpaRepositories(basePackages = {"com.tosan.repository"})
@EntityScan(basePackages = {"com.tosan.model"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}