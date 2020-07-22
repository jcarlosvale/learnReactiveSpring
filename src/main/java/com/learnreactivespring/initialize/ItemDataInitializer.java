package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    private final ItemReactiveRepository repository;
    private final ItemReactiveCappedRepository itemReactiveCappedRepository;
    private final MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }

    private void dataSetUpForCappedCollection() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(aLong -> new ItemCapped(null, "Random Item " + aLong, (100.00 + aLong)));
        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .subscribe(itemCapped -> {
                    log.info("Insert Item is " + itemCapped);
                });
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    }

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 399.99),
                new Item(null, "LG TV", 329.99),
                new Item(null, "Apple Watch TV", 349.99),
                new Item("ABC", "Beats Headphones", 19.99));
    }

    private void initialDataSetup() {
        repository
                .deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(repository::save)
                .thenMany(repository.findAll())
                .subscribe(item -> {
                    System.out.println("Item inserted from CommandLine : " + item);
                });
    }
}
