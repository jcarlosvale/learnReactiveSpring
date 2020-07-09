package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    private final ItemReactiveRepository repository;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
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
