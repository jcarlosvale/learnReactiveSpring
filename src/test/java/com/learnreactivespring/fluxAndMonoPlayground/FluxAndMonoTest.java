package com.learnreactivespring.fluxAndMonoPlayground;


import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

    @Test
    public void fluxCountElementsTest(){
        Flux<String> fluxString = Flux.just("Spring", "Flux", "Reactive")
                .concatWith(Flux.error(new RuntimeException("Some exception")));
        StepVerifier
                .create(fluxString)
                .expectNextCount(3)
                .expectErrorMessage("Some exception")
                //  .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoTest(){
        Mono<String> monoString = Mono.just("Spring");
        StepVerifier
                .create(monoString.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTestWithError(){
        StepVerifier
                .create(Mono.error(new RuntimeException("Mono error")))
                .expectError(RuntimeException.class)
                .verify();
    }
}
