package com.js.hackingspringboot.reactive.ch3;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

import java.util.Date;

@Getter
@ToString(of = {"id", "price", "name", "description"})
@EqualsAndHashCode(of = {"id", "price", "name", "description"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Item {

    @Id
    private String id;
    private String name;
    private String description;
    private double price;
    private String distributorRegion;
    private Date releaseDate;
    private int availableUnits;
    private Point location;
    private boolean active;

    public Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
