package com.learnreactivespring.fluxAndMonoPlayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {

        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some exception")))
                .concatWith(Flux.just("D"))
                .onErrorResume(throwable -> {
                    System.out.println("Exception is " + throwable);
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default", "default1")
                .verifyComplete();
    }

    @Test
    public void fluxErrorReturn() {
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some exception")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default");

        StepVerifier.create(flux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorMap() {
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some exception")))
                .concatWith(Flux.just("D"))
                .onErrorMap(throwable -> new IllegalArgumentException(throwable));

        StepVerifier.create(flux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void fluxErrorMapWithRetry() {
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some exception")))
                .concatWith(Flux.just("D"))
                .onErrorMap(throwable -> new IllegalArgumentException(throwable))
                .retry(2);

        StepVerifier.create(flux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    public void fluxErrorMapWithRetryBackoff() {
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Some exception")))
                .concatWith(Flux.just("D"))
                .onErrorMap(throwable -> new IllegalArgumentException(throwable))
                .retryBackoff(2, Duration.ofSeconds(5));

        StepVerifier.create(flux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(IllegalArgumentException.class)
                .verify();
    }


}
