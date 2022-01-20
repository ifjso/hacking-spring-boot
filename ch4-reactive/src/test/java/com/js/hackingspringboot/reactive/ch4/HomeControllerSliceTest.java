package com.js.hackingspringboot.reactive.ch4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(HomeController.class)
class HomeControllerSliceTest {

    @Autowired
    private WebTestClient client;

    @MockBean
    private InventoryService inventoryService;

    @Test
    void homePage() {
        given(inventoryService.getInventory())
                .willReturn(Flux.just(
                        Item.builder()
                                .id("id1")
                                .name("name1")
                                .description("desc1")
                                .price(1.99)
                                .build(),
                        Item.builder()
                                .id("id2")
                                .name("name2")
                                .description("desc2")
                                .price(9.99)
                                .build()
                ));
        given(inventoryService.getCart("My Cart"))
                .willReturn(Mono.just(Cart.empty("My Cart")));

        client.get().uri("/").exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(exchangeResult -> {
                    assertThat(exchangeResult.getResponseBody()).contains("action=\"/add/id1\"");
                    assertThat(exchangeResult.getResponseBody()).contains("action=\"/add/id2\"");
                });
    }
}
