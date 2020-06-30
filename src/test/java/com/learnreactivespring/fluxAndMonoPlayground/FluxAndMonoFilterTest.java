package com.learnreactivespring.fluxAndMonoPlayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {

    List<String> names = Arrays.asList("adam", "ana", "jack", "jenny");

    @Test
    public void fluxFilterTest() {
        Flux<String> fluxOfStrings = Flux
                        .fromIterable(names)
                        .filter(s -> s.startsWith("a"))
                        .log();
        StepVerifier.create(fluxOfStrings)
                .expectNext("adam")
                .expectNext("ana")
                .verifyComplete();

    }
}
