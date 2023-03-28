package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinkTest {


    @Test
    void sink() {

        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);


        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe( i -> {
            System.out.println("Subscriber 1: " + i);
        });
        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux.subscribe( i -> {
            System.out.println("Subscriber 2: " + i);
        });

        replaySink.tryEmitNext(3);

        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux.subscribe( i -> {
            System.out.println("Subscriber 3: " + i);
        });

    }

    @Test
    void sinkMulticast() {

        Sinks.Many<Integer> multiSink = Sinks.many().multicast().onBackpressureBuffer();


        multiSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multiSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);


        Flux<Integer> integerFlux = multiSink.asFlux();

        integerFlux.subscribe( i -> {
            System.out.println("Subscriber 1: " + i);
        });
        Flux<Integer> integerFlux1 = multiSink.asFlux();
        integerFlux.subscribe( i -> {
            System.out.println("Subscriber 2: " + i);
        });

        multiSink.tryEmitNext(3);

        Flux<Integer> integerFlux2 = multiSink.asFlux();
        integerFlux.subscribe( i -> {
            System.out.println("Subscriber 3: " + i);
        });
    }

    @Test
    void sinkUnicast() {

        Sinks.Many<Integer> uniSink = Sinks.many().unicast().onBackpressureBuffer();


        uniSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        uniSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);


        Flux<Integer> integerFlux = uniSink.asFlux();

        integerFlux.subscribe( i -> {
            System.out.println("Subscriber 1: " + i);
        });
        Flux<Integer> integerFlux1 = uniSink.asFlux();
        integerFlux.subscribe( i -> {
            System.out.println("Subscriber 2: " + i);
        });

        uniSink.tryEmitNext(3);

        Flux<Integer> integerFlux2 = uniSink.asFlux();
        integerFlux.subscribe( i -> {
            System.out.println("Subscriber 3: " + i);
        });
    }

    }
