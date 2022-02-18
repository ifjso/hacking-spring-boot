package com.js.hackingspringboot.reactive.ch9.repository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository repository) {
        return username -> repository.findByName(username)
                .map(user -> User.withDefaultPasswordEncoder()
                        .username(user.getName())
                        .password(user.getPassword())
                        .authorities(user.getRoles().toArray(new String[0]))
                        .build());
    }

    @Bean
    CommandLineRunner userLoader(MongoOperations mongo) {
        return args -> {
            mongo.save(com.js.hackingspringboot.reactive.ch9.repository.User.builder()
                    .name("js")
                    .password("password")
                    .roles(Arrays.asList("ROLE_USER"))
                    .build());
        };
    }
}
