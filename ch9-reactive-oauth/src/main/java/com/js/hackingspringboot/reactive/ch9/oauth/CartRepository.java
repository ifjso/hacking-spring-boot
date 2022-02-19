package com.js.hackingspringboot.reactive.ch9.oauth;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CartRepository extends ReactiveCrudRepository<Cart, String> {
}
