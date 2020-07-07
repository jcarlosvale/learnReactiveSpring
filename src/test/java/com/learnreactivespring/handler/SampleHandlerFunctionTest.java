package com.learnreactivespring.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
public class SampleHandlerFunctionTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach() {
        Flux<Integer> actualFlux =
                webTestClient.get().uri("/functional/flux")
                        .accept(MediaType.APPLICATION_JSON)
                        .exchange().expectStatus().isOk()
                        .returnResult(Integer.class)
                        .getResponseBody();

        StepVerifier.create(actualFlux)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    public void mono() {
        Integer expected = 1;
        webTestClient.get().uri("/functional/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(integerFluxExchangeResult -> {
                            assertEquals(expected, integerFluxExchangeResult.getResponseBody());
                        }
                );
    }
}
