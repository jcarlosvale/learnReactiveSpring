package com.learnreactivespring.fluxAndMonoPlayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "ana", "jack", "jenny");

    @Test
    public void transformUsingUppercaseMapTest() {
        Flux<String> flux = Flux
                .fromIterable(names)
                .map(String::toUpperCase)
                .log();
        StepVerifier.create(flux).expectNext("ADAM", "ANA", "JACK", "JENNY").verifyComplete();
    }

    @Test
    public void transformUsingLengthMapTest() {
        Flux<Integer> flux = Flux
                .fromIterable(names)
                .map(String::length)
                .log();
        StepVerifier.create(flux).expectNext(4,3,4,5).verifyComplete();
    }

    @Test
    public void transformUsingLengthRepeatMapTest() {
        Flux<Integer> flux = Flux
                .fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();
        StepVerifier.create(flux).expectNext(4,3,4,5,4,3,4,5).verifyComplete();
    }

    @Test
    public void transformUsingFlatMapTest() {
            Flux<String> flux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                    .flatMap(s -> {
                        return Flux.fromIterable(convertToList(s));
                    });
            StepVerifier.create(flux.log())
                    .expectNextCount(12)
                    .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapUsingParallelTest() {
        Flux<String> flux = Flux
                .fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2) //Flux<Flux<String>>
                .flatMap((s) ->
                        s.map(this::convertToList)
                        .subscribeOn(Schedulers.parallel()))
                        .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(flux.log())
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapUsingParallelMaintainOrderConcatTest() {
        Flux<String> flux = Flux
                .fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2) //Flux<Flux<String>>
                .concatMap((s) ->
                        s.map(this::convertToList)
                                .subscribeOn(Schedulers.parallel()))
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(flux.log())
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapUsingParallelMaintainOrderFlatMapSequentialTest() {
        Flux<String> flux = Flux
                .fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
                .window(2) //Flux<Flux<String>>
                .flatMapSequential((s) ->
                        s.map(this::convertToList)
                                .subscribeOn(Schedulers.parallel()))
                .flatMap(Flux::fromIterable)
                .log();

        StepVerifier.create(flux.log())
                .expectNextCount(12)
                .verifyComplete();
    }



    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

}
