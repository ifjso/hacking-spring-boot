package com.js.hackingspringboot.reactive.ch6;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class TemplateDatabaseLoader {

    @Bean
    CommandLineRunner initialize(MongoOperations mongo) {
        return args -> {
            mongo.save(Item.builder()
                    .name("Alf alarm clock")
                    .description("kids clock")
                    .price(19.99)
                    .build());
            mongo.save(Item.builder()
                    .name("Smurf TV tray")
                    .description("kids TV tray")
                    .price(24.99)
                    .build());
        };
    }
}
