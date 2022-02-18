package com.js.hackingspringboot.reactive.ch8.server;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Controller
public class RSocketService {

    private final ItemRepository itemRepository;
    private final Sinks.Many<Item> itemSink;

    public RSocketService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;

        // multicast (EmitterProcessor) backpressure buffer size 는 특정 수 (1, 8, 16, 32 ...) 로 동작하는 것 같다.
        // replay (ReplayProcessor) 는 설정한 history size 만큼 정확히 동작한다.
        this.itemSink = Sinks.many().multicast().onBackpressureBuffer(8, false);
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
        return itemSink.asFlux()
                .doOnRequest(l -> System.out.println(l))
                .doOnCancel(() -> System.out.println("cancel"))
                .doOnComplete(() -> System.out.println("complete"));
    }
}
