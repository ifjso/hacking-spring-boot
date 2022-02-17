package com.js.hackingspringboot.reactive.ch8.client;


import static io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_ROUTING;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;
import static org.springframework.http.MediaType.parseMediaType;

import java.net.URI;
import java.time.Duration;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RSocketController {

    private final Mono<RSocketRequester> requester;

    public RSocketController(RSocketRequester.Builder builder) {
        this.requester = Mono.fromSupplier(() ->
                        builder
                                .dataMimeType(APPLICATION_JSON)
                                .metadataMimeType(parseMediaType(MESSAGE_RSOCKET_ROUTING.toString()))
                                .tcp("localhost", 8000))
                .retry(5)
                .cache();
    }

    @PostMapping("/items/request-response")
    Mono<ResponseEntity<?>> addNewItemUsingRSocketRequestResponse(@RequestBody Item item) {
        return requester
                .flatMap(rSocketRequester -> rSocketRequester
                        .route("newItems.request-response")
                        .data(item)
                        .retrieveMono(Item.class))
                .map(savedItem -> ResponseEntity
                        .created(URI.create("/items/request-response"))
                        .body(savedItem));
    }

    @GetMapping(value = "/items/request-stream", produces = APPLICATION_NDJSON_VALUE)
    Flux<Item> findItemUsingRSocketRequestStream() {
        return requester
                .flatMapMany(rSocketRequester -> rSocketRequester
                        .route("newItems.request-stream")
                        .retrieveFlux(Item.class)
                        .delayElements(Duration.ofSeconds(1L)));
    }

}
