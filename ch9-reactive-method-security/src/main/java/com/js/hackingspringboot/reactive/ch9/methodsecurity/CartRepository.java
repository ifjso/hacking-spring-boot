package com.js.hackingspringboot.reactive.ch9.methodsecurity;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CartRepository extends ReactiveCrudRepository<Cart, String> {
}
