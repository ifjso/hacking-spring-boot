package com.js.hackingspringboot.reactive.ch4;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
class CartService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public CartService(ItemRepository itemRepository, CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    Mono<Cart> addToCart(String cartId, String id) {
        return cartRepository.findById(cartId)
                .defaultIfEmpty(new Cart(cartId))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(id))
                        .findAny()
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cart);
                        })
                        .orElseGet(() ->
                                itemRepository.findById(id)
                                        .map(CartItem::new)
                                        .doOnNext(cartItem -> cart.getCartItems().add(cartItem))
                                        .map(cartItem -> cart)))
                .flatMap(cartRepository::save);
    }
}
