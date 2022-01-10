package com.js.hackingspringboot.reactive.ch2;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CartRepository extends ReactiveCrudRepository<Cart, String> {

}
