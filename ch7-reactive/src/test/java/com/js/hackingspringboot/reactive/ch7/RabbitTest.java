package com.js.hackingspringboot.reactive.ch7;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
@ContextConfiguration
class RabbitTest {

    @Container
    private static RabbitMQContainer container =
            new RabbitMQContainer("rabbitmq:3.7.25-management-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", container::getContainerIpAddress);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
    }

    @Test
    void verifyMessagingThroughAmqp() throws InterruptedException {
        webTestClient.post().uri("/items")
                .bodyValue(Item.builder()
                        .name("Alf alarm clock")
                        .description("nothing important")
                        .price(19.99d)
                        .build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody();

        Thread.sleep(1500L);

        webTestClient.post().uri("/items")
                .bodyValue(Item.builder()
                        .name("Smurf TV tray")
                        .description("nothing important")
                        .price(29.99d)
                        .build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody();

        Thread.sleep(2000L);

        itemRepository.findAll()
                .as(StepVerifier::create)
                .expectNextMatches(item -> {
                    assertThat(item.getName()).isEqualTo("Alf alarm clock");
                    assertThat(item.getDescription()).isEqualTo("nothing important");
                    assertThat(item.getPrice()).isEqualTo(19.99d);
                    return true;
                })
                .expectNextMatches(item -> {
                    assertThat(item.getName()).isEqualTo("Smurf TV tray");
                    assertThat(item.getDescription()).isEqualTo("nothing important");
                    assertThat(item.getPrice()).isEqualTo(29.99d);
                    return true;
                })
                .verifyComplete();
    }
}
