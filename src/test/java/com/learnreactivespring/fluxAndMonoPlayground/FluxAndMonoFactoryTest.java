package com.learnreactivespring.fluxAndMonoPlayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam", "ana", "jack", "jenny");

    @Test
    public void fluxUsingIterableTest(){
        Flux<String> fluxOfStrings = Flux.fromIterable(names);

        StepVerifier
                .create(fluxOfStrings.log())
                .expectNext("adam", "ana", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArrayTest(){
        String[] array = {"adam", "ana", "jack", "jenny"};

        Flux<String> fluxOfStrings = Flux.fromArray(array);

        StepVerifier
                .create(fluxOfStrings.log())
                .expectNext("adam", "ana", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStreamTest(){
        Flux<String> fluxOfStrings = Flux.fromStream(names.stream());

        StepVerifier
                .create(fluxOfStrings.log())
                .expectNext("adam", "ana", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void emptyMonoTest(){
        Mono<String> monoOfString = Mono.justOrEmpty(null);

        StepVerifier
                .create(monoOfString.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplierTest(){
        Supplier<String> supplier = () -> "adam";

        Mono<String> monoOfString = Mono.fromSupplier(supplier);

        StepVerifier
                .create(monoOfString.log())
                .expectNext("adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRangeTest(){
        Flux<Integer> flux = Flux.range(1, 5);

        StepVerifier
                .create(flux.log())
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

}
