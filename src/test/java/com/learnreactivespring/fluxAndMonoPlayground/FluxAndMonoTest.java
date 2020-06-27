package com.learnreactivespring.fluxAndMonoPlayground;


import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.awt.*;

public class FluxAndMonoTest {
    @Test
    public void fluxtest() {
        Flux<String> fluxString = Flux.just("Spring", "Flux", "Reactive")
                .concatWith(Flux.error(new RuntimeException("Some exception")))
                .log();
        fluxString.subscribe(System.out::println,
                e -> System.err.println("Exception e: " + e),
                () -> System.out.println("Completed"));
    }

    @Test
    public void fluxWithoutErrorTest(){
        Flux<String> fluxString = Flux.just("Spring", "Flux", "Reactive");
        StepVerifier
                .create(fluxString)
                .expectNext("Spring")
                .expectNext("Flux")
                .expectNext("Reactive")
                .verifyComplete();
    }

    @Test
    public void fluxWithErrorTest(){
        Flux<String> fluxString = Flux.just("Spring", "Flux", "Reactive")
                .concatWith(Flux.error(new RuntimeException("Some exception")));
        StepVerifier
                .create(fluxString)
                .expectNext("Spring")
                .expectNext("Flux")
                .expectNext("Reactive")
                .expectErrorMessage("Some exception")
              //  .expectError(RuntimeException.class)
                .verify();
    }
}
