package com.js.hackingspringboot.reactive.ch7;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SpringAmqpItemService {

   private final ItemRepository itemRepository;

   public SpringAmqpItemService(ItemRepository itemRepository) {
      this.itemRepository = itemRepository;
   }

   @RabbitListener(
           ackMode = "MANUAL",
           bindings = @QueueBinding(
                   value = @Queue,
                   exchange = @Exchange("hacking-spring-boot"),
                   key = "new-items-spring-amqp"))
   public Mono<Void> processNewItemsViaSpringAmqp(Item item) {
      log.debug("Consuming => " + item);
      return itemRepository.save(item).then();
   }
}
