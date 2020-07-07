package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository repository;

    List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphone", 149.99),
            new Item("ABC", "BOSE Headphone", 149.99)

    );

    @Before
    public void setUp() {
        repository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(item -> repository.save(item))
                .doOnNext(item -> System.out.println("inserted item is : " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(repository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(repository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("BOSE Headphone"))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription() {
        StepVerifier.create(repository.findByDescription("LG TV").log("findByDescription: "))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item item = new Item("DEF", "Google Home Mini", 30.00);
        Mono<Item> savedItem = repository.save(item);
        StepVerifier.create(savedItem.log("saveItem: "))
                .expectSubscription()
                .expectNextMatches(item1 -> (item1.getId() != null && item1.getDescription().equals("Google Home Mini")))
                .verifyComplete();
    }

    @Test
    public void updateItem() {

        double newPrice = 520.00;
        Flux<Item> updatedItem = repository.findByDescription("LG TV").map(item -> {
            item.setPrice(newPrice);
            return item;
        }).flatMap(item -> repository.save(item));

        StepVerifier.create(updatedItem.log("updateItem: "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getPrice() == newPrice)
                .verifyComplete();
    }

    @Test
    public void deleteItemById() {
        Mono<Void> deletedItem = repository
                .findById("ABC")
                .map(Item::getId)
                .flatMap(id -> repository.deleteById(id));

        StepVerifier.create(deletedItem.log("deleteItem: "))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(repository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}
