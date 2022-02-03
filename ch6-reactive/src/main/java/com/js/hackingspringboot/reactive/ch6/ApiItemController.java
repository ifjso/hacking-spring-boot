package com.js.hackingspringboot.reactive.ch6;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class ApiItemController {

    private final ItemRepository itemRepository;

    public ApiItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/api/items")
    Flux<Item> findAll() {
        return itemRepository.findAll();
    }

    @GetMapping("/api/items/{id}")
    Mono<Item> findOne(@PathVariable String id) {
        return itemRepository.findById(id);
    }

    @PostMapping("/api/items")
    Mono<ResponseEntity<Item>> addNewItem(@RequestBody Mono<Item> item) {
        return item.flatMap(itemRepository::save)
                .map(savedItem ->
                        ResponseEntity.created(URI.create("/api/items" + savedItem.getId()))
                                .body(savedItem));
    }

    @PutMapping("/api/items/{id}")
    Mono<ResponseEntity<Item>> updateItem(@RequestBody Mono<Item> item, @PathVariable String id) {
        return item.map(content -> new Item(id, content.getName(), content.getDescription(), content.getPrice()))
                .flatMap(itemRepository::save)
                .map(ResponseEntity::ok);
    }
}
