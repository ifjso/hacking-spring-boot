package com.js.hackingspringboot.reactive.ch9.customconfig;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private static final String USER = "USER";
    private static final String INVENTORY = "INVENTORY";

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
    public SecurityWebFilterChain myCustomSecurityPolicy(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/").hasRole(INVENTORY)
                        .pathMatchers(HttpMethod.DELETE, "/**").hasRole(INVENTORY)
                        .anyExchange().authenticated()
                        .and()
                        .httpBasic()
                        .and()
                        .formLogin())
                .csrf().disable()
                .build();
    }

    @Bean
    CommandLineRunner userLoader(MongoOperations mongo) {
        return args -> {
            mongo.save(com.js.hackingspringboot.reactive.ch9.customconfig.User.builder()
                    .name("js")
                    .password("password")
                    .roles(Arrays.asList(role(USER)))
                    .build());
            mongo.save(com.js.hackingspringboot.reactive.ch9.customconfig.User.builder()
                    .name("manager")
                    .password("password")
                    .roles(Arrays.asList(role(USER), role(INVENTORY)))
                    .build());
        };
    }

    private String role(String auth) {
        return "ROLE_" + auth;
    }
}
