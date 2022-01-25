package com.js.hackingspringboot.reactive.ch6;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CartItem {

    private Item item;
    private int quantity;

    private CartItem(Item item) {
        this.item = item;
        this.quantity = 1;
    }

    public void increment() {
        this.quantity++;
    }

    public void decrement() {
        this.quantity--;
    }

    public static CartItem from(Item item) {
        return new CartItem(item);
    }
}
