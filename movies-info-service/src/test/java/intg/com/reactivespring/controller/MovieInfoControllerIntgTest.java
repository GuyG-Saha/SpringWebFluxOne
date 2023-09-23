package com.reactivespring.controller;

import com.fasterxml.jackson.core.JsonToken;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieInfoControllerIntgTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;
    @Autowired
    WebTestClient webTestClient;
    final static String MOVIE_INFO_URI = "/v1/movieinfos";
    @BeforeEach
    void setUp() {
        var moviesData =
                List.of(new MovieInfo("sg5", "Batman Begins", 2005, List.of("Christian Bale",
                                "Michael Cane"), LocalDate.parse("2005-06-15")),
                        new MovieInfo("df4", "The Godfather I", 1972, List.of("Marlon Brando", "Al Pacino"),
                                LocalDate.parse("1972-03-24")),
                        new MovieInfo("se7", "Pulp Fiction", 1994, List.of("Emma Thurman, John Travolta",
                                "Samuel L. Jackson", "Bruce Willis"), LocalDate.parse("1994-10-14"))
                );
        movieInfoRepository.saveAll(moviesData)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void GetAllMovieInfosReturnsFourObjects() {
        webTestClient
                .get()
                .uri(MOVIE_INFO_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(4);
    }
    @Test
    void addMovieInfo() {
        var movieInfo = new MovieInfo("test1", "Pulp Fiction", 1994, List.of("Emma Thurman, John Travolta",
                "Samuel L. Jackson", "Bruce Willis"), LocalDate.parse("1994-10-14"));
        webTestClient
                .post()
                .uri(MOVIE_INFO_URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.nonNull(savedMovieInfo);
                    assert Objects.equals(savedMovieInfo.getId(), "test1");
                });
    }

    @Test
    void GetPulFictionMovieInfoById() {
        var movieInfoId = "se7";
        webTestClient
                .get()
                .uri(MOVIE_INFO_URI + "/" + movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Pulp Fiction");
    }
    @Test
    void UnknownMovieInfoIdReturnsNullBody() {
        var movieInfoId = "unknown";
        webTestClient
                .get()
                .uri(MOVIE_INFO_URI + "/" + movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var desiredMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.isNull(desiredMovieInfo);
                });
    }
    @Test
    void updateMovieInfo() {
        var updatedMovieInfo = new MovieInfo("sg5", "Batman Begins II", 2008, List.of("Christian Bale",
                "Michael Cane"), LocalDate.parse("2008-09-13"));
        webTestClient
                .put()
                .uri(MOVIE_INFO_URI + "/" + updatedMovieInfo.getId())
                .bodyValue(updatedMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.nonNull(savedMovieInfo);
                    assert Objects.equals(savedMovieInfo.getYear(), 2008);
                    assert Objects.equals(savedMovieInfo.getName(), "Batman Begins II");
                    assert Objects.equals(savedMovieInfo.getReleaseDate(), LocalDate.parse("2008-09-13"));
                });
    }
    @Test
    void deleteBatmanMovieInfo() {
        var movieInfoId = "sg5";
        webTestClient
                .delete()
                .uri(MOVIE_INFO_URI + "/" + movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }
    @Test
    void deleteMovieInfo() {
        MovieInfo movieInfoToBeDeleted = new MovieInfo("a000", "The Open House", 2018, List.of(),
                LocalDate.parse("2018-01-19"));
        webTestClient
                .post()
                .uri(MOVIE_INFO_URI)
                .bodyValue(movieInfoToBeDeleted)
                .exchange()
                .expectStatus()
                .isCreated();
        webTestClient
                .delete()
                .uri(MOVIE_INFO_URI + "/" + movieInfoToBeDeleted.getId())
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }
}