package com.learnreactivespring.fluxAndMonoPlayground;

import org.junit.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> flux = Flux.range(1, 10).log();

        StepVerifier.create(flux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {
        Flux<Integer> flux = Flux.range(1, 10).log();

        flux.subscribe(integer -> System.out.println("Element is " + integer),
                throwable -> System.err.println("Exception is " + throwable),
                () -> System.out.println("Done"),
                subscription -> subscription.request(2));
    }

    @Test
    public void backPressureCancel() {
        Flux<Integer> flux = Flux.range(1, 10).log();

        flux.subscribe(integer -> System.out.println("Element is " + integer),
                throwable -> System.err.println("Exception is " + throwable),
                () -> System.out.println("Done"),
                subscription -> subscription.cancel());
    }

    @Test
    public void backPressureCustomized() {
        Flux<Integer> flux = Flux.range(1, 10).log();

        flux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Element is " + value);
                if (value == 4) {
                    cancel();
                }
            }
        });
    }


}
