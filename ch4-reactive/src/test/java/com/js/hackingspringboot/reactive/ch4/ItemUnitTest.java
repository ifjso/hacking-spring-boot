package com.js.hackingspringboot.reactive.ch4;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ItemUnitTest {

    @Test
    void itemBasicsShouldWork() {
        Item sampleItem = Item.builder()
                .id("item1")
                .name("TV tray")
                .description("Alf TV tray")
                .price(19.99)
                .build();

        assertThat(sampleItem.getId()).isEqualTo("item1");
        assertThat(sampleItem.getName()).isEqualTo("TV tray");
        assertThat(sampleItem.getDescription()).isEqualTo("Alf TV tray");
        assertThat(sampleItem.getPrice()).isEqualTo(19.99);

        assertThat(sampleItem.toString()).isEqualTo(
                "Item(id=item1, name=TV tray, description=Alf TV tray, price=19.99)");

        Item sampleItem2 = Item.builder()
                .id("item1")
                .name("TV tray")
                .description("Alf TV tray")
                .price(19.99)
                .build();
        assertThat(sampleItem).isEqualTo(sampleItem2);
    }
}
