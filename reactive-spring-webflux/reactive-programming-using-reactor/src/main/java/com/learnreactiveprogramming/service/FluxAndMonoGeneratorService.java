package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class FluxAndMonoGeneratorService {


    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex","ben","chloe"))
                .log(); // db or a remote service call
    }

    public Flux<String> namesFlux_map() {
        return Flux.fromIterable(List.of("alex","ben","chloe"))
                .map(String::toUpperCase)
                .log(); // db or a remote service call
    }

    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(List.of("alex","ben","chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;

    }

    public Flux<String> namesFlux_split() {
        return Flux.fromIterable(List.of("alex","ben","chloe"))
                .map(String::toUpperCase)
                .flatMap(this::splitString)
                .log(); // db or a remote service call
    }

    private Flux<String> splitString (String name) {
        String[] nameSplit = name.split("");
        return Flux.fromArray(nameSplit);
    }

    public Flux<String> namesFlux_flatmap_async() {
        return Flux.fromIterable(List.of("alex","chloe"))
                .map(String::toUpperCase)
                .flatMap(this::splitString_withDelay)
                .log(); // db or a remote service call
    }
    private Flux<String> splitString_withDelay (String name) {
        String[] nameSplit = name.split("");
        var delay =  new Random().nextInt(1000);
        return Flux.fromArray(nameSplit).delayElements(Duration.ofMillis(delay));
    }
    public Mono<String> namesMono() {
        return Mono.just("alex").log();
    }
    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux().subscribe(name -> System.out.println(name));
        fluxAndMonoGeneratorService.namesMono().subscribe(name -> System.out.println(name));

    }
}
