package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository repository;

    List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 420.0),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphone", 149.99)
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
                .expectNextCount(4)
                .verifyComplete();
    }
}
