package com.js.hackingspringboot.reactive.ch8.server;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class RSocketService {

    private final ItemRepository itemRepository;
    private final Sinks.Many<Item> itemSink;

    public RSocketService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.itemSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @MessageMapping("newItems.request-response")
    public Mono<Item> processNewItemsViaRSocketRequestResponse(Item item) {
        return itemRepository.save(item)
                .doOnNext(itemSink::tryEmitNext);
    }

    @MessageMapping("newItems.request-stream")
    public Flux<Item> findItemsViaRSocketRequestStream() {
        return itemRepository.findAll()
                .doOnNext(itemSink::tryEmitNext);
    }

    @MessageMapping("newItems.fire-and-forget")
    public Mono<Void> processNewItemsViaRSocketAndForget(Item item) {
        return itemRepository.save(item)
                .doOnNext(itemSink::tryEmitNext)
                .then();
    }

    @MessageMapping("newItems.monitor")
    public Flux<Item> monitorNewItems() {
        return itemSink.asFlux();
    }
}
