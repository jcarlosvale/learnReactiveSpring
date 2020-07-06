package com.learnreactivespring.fluxAndMonoPlayground;

import org.junit.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {
    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> flux = Flux.interval(Duration.ofMillis(100)).take(3).log();

        flux.subscribe(aLong -> System.out.println("Value : " + aLong));

        Thread.sleep(3000);
    }
}
