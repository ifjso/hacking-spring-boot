package com.js.hackingspringboot.reactive.ch9.methodsecurity;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.js.hackingspringboot.reactive.ch9.methodsecurity.SecurityConfig.INVENTORY;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
public class ApiItemController {

    private static final SimpleGrantedAuthority ROLE_INVENTORY =
            new SimpleGrantedAuthority("ROLE_" + INVENTORY);

    private final ItemRepository itemRepository;

    public ApiItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/api")
    Mono<RepresentationModel<?>> root() {
        ApiItemController controller = methodOn(ApiItemController.class);

        Mono<Link> selfLink = linkTo(controller.root())
                .withSelfRel()
                .toMono();
        Mono<Link> itemAggregateLink = linkTo(controller.findAll(null))
                .withRel(IanaLinkRelations.ITEM)
                .toMono();

        return Mono.zip(selfLink, itemAggregateLink)
                .map(links -> Links.of(links.getT1(), links.getT2()))
                .map(links -> new RepresentationModel<>(links.toList()));
    }

    @GetMapping("/api/items")
    Mono<CollectionModel<EntityModel<Item>>> findAll(Authentication auth) {
        ApiItemController controller = methodOn(ApiItemController.class);

        Mono<Link> selfLink = linkTo(controller.findAll(auth))
                .withSelfRel()
                .toMono();
        Mono<Links> allLinks;

        if (auth.getAuthorities().contains(ROLE_INVENTORY)) {
            Mono<Link> addNewLink = linkTo(controller.addNewItem(null, auth))
                    .withRel("add")
                    .toMono();
            allLinks = Mono.zip(selfLink, addNewLink)
                    .map(links -> Links.of(links.getT1(), links.getT2()));
        } else {
            allLinks = selfLink.map(Links::of);
        }

        return allLinks.flatMap(links -> itemRepository.findAll()
                .flatMap(item -> findOne(item.getId(), auth))
                .collectList()
                .map(entityModels -> CollectionModel.of(entityModels, links)));

    }

    @GetMapping("/api/items/{id}")
    Mono<EntityModel<Item>> findOne(@PathVariable String id, Authentication auth) {
        ApiItemController controller = methodOn(ApiItemController.class);

        Mono<Link> selfLink = linkTo(controller.findOne(id, auth))
                .withSelfRel()
                .toMono();
        Mono<Link> aggregateLink = linkTo(controller.findAll(auth))
                .withRel(IanaLinkRelations.ITEM)
                .toMono();
        Mono<Links> allLinks;

        if (auth.getAuthorities().contains(ROLE_INVENTORY)) {
            Mono<Link> deleteLink = linkTo(controller.deleteItem(id))
                    .withRel("delete")
                    .toMono();
            allLinks = Mono.zip(selfLink, aggregateLink, deleteLink)
                    .map(links -> Links.of(links.getT1(), links.getT2(), links.getT3()));
        } else {
            allLinks = Mono.zip(selfLink, aggregateLink)
                    .map(links -> Links.of(links.getT1(), links.getT2()));
        }

        return itemRepository.findById(id)
                .zipWith(allLinks)
                .map(o -> EntityModel.of(o.getT1(), o.getT2()));

    }

    @PreAuthorize("hasRole('" + INVENTORY + "')")
    @PostMapping("/api/items/add")
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Item item, Authentication auth) {
        return itemRepository.save(item)
                .map(Item::getId)
                .flatMap(id -> findOne(id, auth))
                .map(newModel -> ResponseEntity
                        .created(newModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                        .build());
    }

    @PreAuthorize("hasRole('" + INVENTORY + "')")
    @DeleteMapping("/api/items/delete/{id}")
    Mono<ResponseEntity<?>> deleteItem(@PathVariable String id) {
        return itemRepository.deleteById(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PutMapping("/api/items/{id}")
    Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<EntityModel<Item>> item,
                                       @PathVariable String id,
                                       Authentication auth) {
        return item.mapNotNull(EntityModel::getContent)
                .map(content -> Item.builder()
                        .id(id)
                        .name(content.getName())
                        .description(content.getDescription())
                        .price(content.getPrice())
                        .build())
                .flatMap(itemRepository::save)
                .then(findOne(id, auth))
                .map(model -> ResponseEntity.noContent()
                        .location(model.getRequiredLink(IanaLinkRelations.SELF)
                                .toUri())
                        .build());

    }
}
