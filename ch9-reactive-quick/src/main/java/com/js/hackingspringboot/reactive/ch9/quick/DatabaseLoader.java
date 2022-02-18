package com.js.hackingspringboot.reactive.ch9.quick;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class DatabaseLoader {

    @Bean
    CommandLineRunner initialize(MongoOperations mongo) {
        return args -> {
            mongo.save(Item.builder()
                    .name("Alf alarm clock")
                    .description("kids clock")
                    .price(19.99d)
                    .build());
            mongo.save(Item.builder()
                    .name("Smurf TV tray")
                    .description("kids TV tray")
                    .price(24.99d)
                    .build());
        };
    }
}
