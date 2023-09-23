package com.reactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Iterator;

@RestController
public class FluxAndMonoController {

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> stream() {
        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping("/flux")
    public Flux<Integer> flux(){
        return Flux.just(1,2,3)
                .log();
    }

    @GetMapping("/mono")
    public Mono<String> helloWorldMono(){
        return Mono.just("hello-world")
                .log();
    }
    @GetMapping("/flux/natural-odds/{limit}")
    public Flux<Integer> fluxNaturalOdds(@PathVariable int limit) {
        final int[] x = {1};
        return Flux.fromIterable(() -> new Iterator<Integer>() {
            @Override
            public boolean hasNext() {
                return x[0] < limit;
            }

            @Override
            public Integer next() {
                return x[0] += 2;
            }
        }).log();
    }
}
