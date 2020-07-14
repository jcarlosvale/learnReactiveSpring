package com.learnreactivespring.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach() {
        Flux<Integer> actualFlux =
                webTestClient.get().uri("/flux").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                        .returnResult(Integer.class).getResponseBody();

        StepVerifier.create(actualFlux)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    public void flux_approach2() {
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void flux_approach3() {
        List<Integer> expected = Arrays.asList(1,2,3,4);
        EntityExchangeResult<List<Integer>> entityExchangeResult =
                webTestClient.get().uri("/flux").accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk()
                        .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(Integer.class)
                        .returnResult();
        assertEquals(expected, entityExchangeResult.getResponseBody());
    }

    @Test
    public void flux_approach4() {
        List<Integer> expected = Arrays.asList(1,2,3,4);
        webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response -> {
                    assertEquals(expected, response.getResponseBody());
                });
    }

    @Test
    public void flux_infinite_approach() {
        Flux<Long> actualFlux =
                webTestClient.get().uri("/fluxstream")
                        .accept(MediaType.APPLICATION_STREAM_JSON)
                        .exchange()
                        .expectStatus().isOk()
                        .returnResult(Long.class).getResponseBody();

        StepVerifier.create(actualFlux)
                .expectSubscription()
                .expectNext(0L,1L,2L,3L,4L)
                .thenCancel()
                .verify();
    }

    @Test
    public void mono() {
        Integer expected = 1;
        webTestClient.get().uri("/mono")
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
