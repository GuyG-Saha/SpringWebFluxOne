package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = FluxAndMonoController.class)
public class FluxAndMonoControllerTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void test() {
        webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(3);
    }

    @Test
    void testResponseBody() {
        var response =
                webTestClient
                .get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();
        StepVerifier.create(response)
                .expectNext(1,2,3)
                .verifyComplete();
    }
    @Test
    void monoTestResponseBody() {
        var response =
                webTestClient
                        .get()
                        .uri("/mono")
                        .exchange()
                        .expectStatus()
                        .is2xxSuccessful()
                        .returnResult(String.class)
                        .getResponseBody();
        StepVerifier.create(response)
                .expectNext("hello-world")
                .verifyComplete();
    }
    @Test
    void streamingTestResponseBody() {
        var response =
                webTestClient
                        .get()
                        .uri("/flux/natural-odds/100")
                        .exchange()
                        .expectStatus()
                        .is2xxSuccessful()
                        .returnResult(Integer.class)
                        .getResponseBody();
        StepVerifier.create(response)
                .expectNext(3,5,7)
                .thenCancel()
                .verify();
    }
}
