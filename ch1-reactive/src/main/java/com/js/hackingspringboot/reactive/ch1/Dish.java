package com.js.hackingspringboot.reactive.ch1;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Dish {

    private String description;
    private boolean delivered = false;

    public static Dish deliver(Dish dish) {
        Dish deliveredDish = new Dish(dish.description);
        deliveredDish.delivered = true;
        return deliveredDish;
    }

    Dish(String description) {
        this.description = description;
    }
}
