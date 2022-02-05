package com.js.hackingspringboot.reactive.ch6;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
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

@WebFluxTest(controllers = HypermediaItemController.class)
@AutoConfigureRestDocs
class HypermediaItemControllerDocumentationTest {

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
        given(itemRepository.findById("item-1")).willReturn(
                Mono.just(Item.builder()
                        .id("item-1")
                        .name("Alf alarm clock")
                        .description("nothing I really need")
                        .price(19.99d)
                        .build()));

        webTestClient.get().uri("/hypermedia/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("findAll-hypermedia", preprocessResponse(prettyPrint())));

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

        webTestClient.get().uri("/hypermedia/items/item-1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document(
                        "findOne-hypermedia",
                        preprocessResponse(prettyPrint()),
                        links(
                                linkWithRel("self").description("이 `Item` 에 대한 공식 링크"),
                                linkWithRel("item").description("`Item` 목록 링크"))));
    }

    @Test
    void postNewItem() {
        Item item = Item.builder()
                .id("item-1")
                .name("Alf alarm clock")
                .description("nothing I really need")
                .price(19.99d)
                .build();
        given(itemRepository.findById(any(String.class))).willReturn(
                Mono.just(item));
        given(itemRepository.save(any())).willReturn(
                Mono.just(item));

        webTestClient.post().uri("/hypermedia/items")
                .body(Mono.just(Item.builder()
                        .name("Alf alarm clock")
                        .description("nothing I really need")
                        .price(19.99d)
                        .build()), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document(
                        "post-new-item-hypermedia",
                        preprocessResponse(prettyPrint())));
    }

    @Test
    void updateItem() {
        Item item = Item.builder()
                .id("1")
                .name("Alf alarm clock")
                .description("updated")
                .price(19.99d)
                .build();
        given(itemRepository.save(item)).willReturn(
                Mono.just(item));
        given(itemRepository.findById("1")).willReturn(
                Mono.just(item));

        webTestClient.put().uri("/hypermedia/items/1")
                .bodyValue(item)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(document("update-item-hypermedia", preprocessResponse(prettyPrint())));
    }
}
