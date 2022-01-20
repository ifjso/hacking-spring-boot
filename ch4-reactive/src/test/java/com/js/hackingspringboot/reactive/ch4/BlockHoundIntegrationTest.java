package com.js.hackingspringboot.reactive.ch4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BlockHoundIntegrationTest {

    @InjectMocks
    private AltInventoryService sut;

    @Mock
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        given(cartRepository.findById(anyString()))
                .willReturn(Mono.<Cart>empty().hide());
    }

    @Test
    void blockHoundShouldTrapBlockingCall() {
        Mono.delay(Duration.ofSeconds(1))
                .flatMap(tick -> sut.addItemToCart("My Cart", "item1"))
                .as(StepVerifier::create)
                .verifyErrorSatisfies(throwable -> {
                    assertThat(throwable).hasMessageContaining(
                            "block()/blockFirst()/blockLast() are blocking");
                });
    }
}
