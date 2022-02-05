package com.js.hackingspringboot.reactive.ch6;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL_FORMS;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication
@EnableHypermediaSupport(type = {HAL, HAL_FORMS})
public class HackingSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(HackingSpringBootApplication.class, args);
    }

}
