package com.js.hackingspringboot.reactive.ch2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CartItem {

    private Item item;
    private int quantity;

    public CartItem(Item item) {
        this.item = item;
        this.quantity = 1;
    }
}
