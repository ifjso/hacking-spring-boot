package com.js.hackingspringboot.reactive.ch4;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class InventoryServiceUnitTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private InventoryService sut;

    @BeforeEach
    void setUp() {
        Item sampleItem = Item.builder()
                .id("item1")
                .name("TV tray")
                .description("Alf TV tray")
                .price(19.99)
                .build();
        CartItem sampleCartItem = CartItem.from(sampleItem);
        Cart sampleCart = Cart.of("My Cart", Collections.singletonList(sampleCartItem));

        given(cartRepository.findById(anyString())).willReturn(Mono.empty());
        given(itemRepository.findById(anyString())).willReturn(Mono.just(sampleItem));
        given(cartRepository.save(any(Cart.class))).willReturn(Mono.just(sampleCart));
    }

    @Test
    void addItemToEmptyCartShouldProduceOneCartItem() {
        sut.addItemToCart("My Cart", "item1")
                .as(StepVerifier::create)
                .expectNextMatches(cart -> {
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                            .containsExactlyInAnyOrder(1);

                    assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                            .containsExactly(Item.builder()
                                    .id("item1")
                                    .build());

                    return true;
                })
                .verifyComplete();
    }

    @Test
    void alternativeWayToTest() {
        StepVerifier.create(sut.addItemToCart("My Cart", "item1"))
                .expectNextMatches(cart -> {
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                            .containsExactlyInAnyOrder(1);

                    assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                            .containsExactly(Item.builder()
                                    .id("item1")
                                    .build());

                    return true;
                })
                .verifyComplete();
    }
}