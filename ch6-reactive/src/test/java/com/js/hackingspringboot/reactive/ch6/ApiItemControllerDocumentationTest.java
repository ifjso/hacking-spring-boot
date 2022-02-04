package com.js.hackingspringboot.reactive.ch6;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = ApiItemController.class)
@AutoConfigureRestDocs
class ApiItemControllerDocumentationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void findingAllItems() {
        given(itemRepository.findAll()).willReturn(
                Flux.just(Item.builder()
                        .id("item-1")
                        .name("Alf alarm clock")
                        .description("nothing I really need")
                        .price(19.99d)
                        .build()));

        webTestClient.get().uri("/api/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("findAll", preprocessResponse(prettyPrint())));
    }

    @Test
    void postNewItem() {
        Item item = Item.builder()
                .id("1")
                .name("Alf alarm clock")
                .description("nothing important")
                .price(19.99d)
                .build();
        given(itemRepository.save(any())).willReturn(
                Mono.just(item));

        webTestClient.post().uri("/api/items")
                .bodyValue(item)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document(
                        "post-new-item",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void findOneItem() {
        given(itemRepository.findById("item-1")).willReturn(
                Mono.just(Item.builder()
                        .id("item-1")
                        .name("Alf alarm clock")
                        .description("nothing I really need")
                        .price(19.99d)
                        .build()));

        webTestClient.get().uri("/api/items/item-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("findOne", preprocessResponse(prettyPrint())));
    }

    @Test
    void updateItem() {
        Item item = Item.builder()
                .id("1")
                .name("Alf alarm clock")
                .description("updated")
                .price(19.99d)
                .build();
        given(itemRepository.save(any())).willReturn(
                Mono.just(item));

        webTestClient.put().uri("/api/items/1")
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("update-item", preprocessResponse(prettyPrint())));
    }
}
