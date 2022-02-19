package com.js.hackingspringboot.reactive.ch9.methodsecurity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cart {

    @Id
    private String id;
    private List<CartItem> cartItems;

    @Builder
    private Cart(String id, List<CartItem> cartItems) {
        this.id = id;
        this.cartItems = cartItems;
    }

    public static Cart from(String id) {
        return new Cart(id, new ArrayList<>());
    }
}
