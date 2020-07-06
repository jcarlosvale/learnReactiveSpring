package com.learnreactivespring.fluxAndMonoPlayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void testingWithoutVirtualTime() {
        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1)).take(3).log();

        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void testingUsingVirtualTime() {

        VirtualTimeScheduler.getOrSet();

        Flux<Long> flux = Flux.interval(Duration.ofSeconds(1)).take(3).log();

        StepVerifier.withVirtualTime(() -> flux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

}
