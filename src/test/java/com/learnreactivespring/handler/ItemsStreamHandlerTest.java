package com.learnreactivespring.handler;

import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemReactiveCappedRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.learnreactivespring.constants.ItemConstants.ITEM_STREAM_FUNCTIONAL_STREAM_END_POINT_V1;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemsStreamHandlerTest {

    @Autowired
    private ItemReactiveCappedRepository itemReactiveCappedRepository;
    @Autowired
    private MongoOperations mongoOperations;
    @Autowired
    WebTestClient webTestClient;


    @Before
    public void setUp() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(aLong -> new ItemCapped(null, "Random Item " + aLong, (100.00 + aLong)))
                .take(5);
        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .doOnNext(itemCapped -> {
                    System.out.println("Insert Item is " + itemCapped);
                })
                .blockLast();
    }

    @Test
    public void testStreamAllItems() {
        Flux<ItemCapped> itemsCappedFlux =
                webTestClient.get().uri(ITEM_STREAM_FUNCTIONAL_STREAM_END_POINT_V1)
                        .exchange()
                        .expectStatus().isOk()
                        .returnResult(ItemCapped.class).getResponseBody().take(5);

        StepVerifier.create(itemsCappedFlux.log("Value from network: "))
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}
