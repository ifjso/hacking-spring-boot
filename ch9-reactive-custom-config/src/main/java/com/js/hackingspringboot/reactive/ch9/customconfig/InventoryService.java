package com.js.hackingspringboot.reactive.ch9.customconfig;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public InventoryService(ItemRepository itemRepository, CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    public Mono<Cart> getCart(String cartId) {
        return cartRepository.findById(cartId);
    }

    public Flux<Item> getInventory() {
        return itemRepository.findAll();
    }

    Mono<Item> saveItem(Item item) {
        return itemRepository.save(item);
    }

    Mono<Void> deleteItem(String id) {
        return itemRepository.deleteById(id);
    }

    Mono<Cart> addItemToCart(String cartId, String itemId) {
        return cartRepository.findById(cartId)
                .defaultIfEmpty(Cart.from(cartId))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                        .findAny()
                        .map(cartItem -> {
                            cartItem.increment();
                            return Mono.just(cart);
                        })
                        .orElseGet(() -> itemRepository.findById(itemId)
                                .map(CartItem::from)
                                .map(cartItem -> {
                                    cart.getCartItems().add(cartItem);
                                    return cart;
                                })))
                .flatMap(cartRepository::save);
    }

    Mono<Cart> removeOneFromCart(String cartId, String itemId) {
        return cartRepository.findById(cartId)
                .defaultIfEmpty(Cart.from(cartId))
                .flatMap(cart -> cart.getCartItems().stream()
                        .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                        .findAny()
                        .map(cartItem -> {
                            cartItem.decrement();
                            return Mono.just(cart);
                        })
                        .orElse(Mono.empty()))
                .map(cart -> Cart.builder()
                        .id(cart.getId())
                        .cartItems(cart.getCartItems().stream()
                                .filter(cartItem -> cartItem.getQuantity() > 0)
                                .collect(Collectors.toList()))
                        .build())
                .flatMap(cartRepository::save);
    }
}
