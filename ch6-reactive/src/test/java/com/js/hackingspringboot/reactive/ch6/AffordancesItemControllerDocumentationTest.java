package com.js.hackingspringboot.reactive.ch6;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = AffordancesItemController.class)
@AutoConfigureRestDocs
class AffordancesItemControllerDocumentationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void findSingleItemAffordances() {
        given(itemRepository.findById("item-1")).willReturn(
                Mono.just(Item.builder()
                        .id("item-1")
                        .name("Alf alarm clock")
                        .description("nothing I really need")
                        .price(19.99d)
                        .build()));

        webTestClient.get().uri("/affordances/items/item-1")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document(
                        "single-item-affordances",
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void findAggregateRootItemAffordances() {
        given(itemRepository.findAll()).willReturn(
                Flux.just(Item.builder()
                        .id("item-1")
                        .name("Alf alarm clock")
                        .description("nothing I really need")
                        .price(19.99d)
                        .build()));
        given(itemRepository.findById(anyString())).willReturn(
                Mono.just(Item.builder()
                        .id("item-1")
                        .name("Alf alarm clock")
                        .description("nothing I really need")
                        .price(19.99d)
                        .build()));

        webTestClient.get().uri("/affordances/items")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document(
                        "aggregate-root-affordances",
                        preprocessResponse(prettyPrint())));
    }
}
