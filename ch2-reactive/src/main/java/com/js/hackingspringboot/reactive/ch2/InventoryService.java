package com.js.hackingspringboot.reactive.ch2;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
class InventoryService {

    private final ItemRepository itemRepository;
    private final ReactiveFluentMongoOperations fluentMongoOperations;

    public InventoryService(ItemRepository itemRepository, ReactiveFluentMongoOperations fluentMongoOperations) {
        this.itemRepository = itemRepository;
        this.fluentMongoOperations = fluentMongoOperations;
    }

    Flux<Item> searchByExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0);

        ExampleMatcher matcher = (useAnd ? ExampleMatcher.matchingAll() : ExampleMatcher.matchingAny())
                .withStringMatcher(StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths("price", "availableUnits", "active");

        Example<Item> probe = Example.of(item, matcher);

        return itemRepository.findAll(probe);
    }

    Flux<Item> searchByFluentExample(String name, String description) {
        return fluentMongoOperations.query(Item.class)
                .matching(query(where("name").is(name).and("description").is(description)))
                .all();
    }

    Flux<Item> searchByFluentExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0);

        ExampleMatcher matcher = (useAnd ? ExampleMatcher.matchingAll() : ExampleMatcher.matchingAny())
                .withStringMatcher(StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths("price", "availableUnits", "active");

        return fluentMongoOperations.query(Item.class)
                .matching(query(byExample(Example.of(item, matcher))))
                .all();
    }
}
