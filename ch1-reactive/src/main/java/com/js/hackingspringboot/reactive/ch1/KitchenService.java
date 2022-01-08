package com.js.hackingspringboot.reactive.ch1;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class KitchenService {

    private List<Dish> menu = List.of(
        new Dish("Sesame chicken"),
        new Dish("Lo mein noodles, plain"),
        new Dish("Sweet & sour beef"));

    private Random picker = new Random();

    Flux<Dish> getDishes() {
        return Flux.<Dish>generate(sink -> sink.next(randomDish()))
            .delayElements(Duration.ofMillis(250));
    }

    private Dish randomDish() {
        return menu.get(picker.nextInt(menu.size()));
    }
}
