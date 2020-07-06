package com.learnreactivespring.fluxAndMonoPlayground;

import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {
        Flux<String> flux = Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1));

        flux.subscribe(s -> System.out.println("Subscriber 1 Element is " + s));

        Thread.sleep(2000);

        flux.subscribe(s -> System.out.println("Subscriber 2 Element is " + s));

        Thread.sleep(4000);
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {
        Flux<String> flux = Flux.just("A", "B", "C", "D", "E", "F").delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = flux.publish();

        connectableFlux.connect();
        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 Element is " + s));
        Thread.sleep(2000);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 Element is " + s));
        Thread.sleep(4000);
    }

}
