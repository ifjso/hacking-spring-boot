package com.js.hackingspringboot.reactive.ch5;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Cart {

    @Id
    private String id;
    private List<CartItem> cartItems;

    @Builder
    public Cart(String id, List<CartItem> cartItems) {
        this.id = id;
        this.cartItems = cartItems;
    }

    public static Cart empty(String id) {
        return new Cart(id, new ArrayList<>());
    }

    public static Cart of(String id, List<CartItem> cartItems) {
        return new Cart(id, cartItems);
    }
}
