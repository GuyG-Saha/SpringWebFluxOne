package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private MovieInfoService movieInfoServiceMock;
    final static String MOVIE_INFO_URI = "/v1/movieinfos";

    @Test
    void getAllMovies() {
        var movieInfos = List.of(
                new MovieInfo("sg5", "Batman Begins", 2005, List.of("Christian Bale",
                        "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo("df4", "The Godfather I", 1972, List.of("Marlon Brando", "Al Pacino"),
                        LocalDate.parse("1972-03-24")),
                new MovieInfo("se7", "Pulp Fiction", 1994, List.of("Emma Thurman, John Travolta",
                        "Samuel L. Jackson", "Bruce Willis"), LocalDate.parse("1994-10-14"))
        );
        when(movieInfoServiceMock.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieInfos));

        webTestClient
                .get()
                .uri(MOVIE_INFO_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }
    @Test
    void getMovieInfoById() {
        var movieInfos = List.of(
                new MovieInfo("sg5", "Batman Begins", 2005, List.of("Christian Bale",
                        "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo("df4", "The Godfather I", 1972, List.of("Marlon Brando", "Al Pacino"),
                        LocalDate.parse("1972-03-24")),
                new MovieInfo("se7", "Pulp Fiction", 1994, List.of("Emma Thurman, John Travolta",
                        "Samuel L. Jackson", "Bruce Willis"), LocalDate.parse("1994-10-14"))
        );
        String movieInfoId = "se7";
        when(movieInfoServiceMock.findMovieInfoById(movieInfoId)).thenReturn(Mono.just(movieInfos.get(2)));
        webTestClient
                .get()
                .uri(MOVIE_INFO_URI + "/" + movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var pulFictionMovie = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.nonNull(pulFictionMovie.getName());
                    assert Objects.equals(pulFictionMovie.getName(), "Pulp Fiction");
                });
    }
    @Test
    void addMovieInfo() {
        var newMovieInfo = new MovieInfo("aac3", "Once Upon a Time in Hollywood",
                2019, List.of("Leonardo DiCaprio, Al Pacino",
                "Brad Pitt", "Margot Robbie", "Luke Perry"), LocalDate.parse("2019-07-26"));
        when(movieInfoServiceMock.addMovieInfo(newMovieInfo)).thenReturn(Mono.just(newMovieInfo));
        webTestClient
                .post()
                .uri(MOVIE_INFO_URI)
                .bodyValue(newMovieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.nonNull(savedMovieInfo);
                    assert Objects.equals(savedMovieInfo.getId(), "aac3");
                });
    }
    @Test
    void addMovieInfo_validation() {
        var newMovieInfo = new MovieInfo("aac3", "",
                -2019, List.of("Leonardo DiCaprio, Al Pacino",
                "Brad Pitt", "Margot Robbie", "Luke Perry"), LocalDate.parse("2019-07-26"));
        when(movieInfoServiceMock.addMovieInfo(newMovieInfo)).thenReturn(Mono.just(newMovieInfo));
        webTestClient
                .post()
                .uri(MOVIE_INFO_URI)
                .bodyValue(newMovieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                   var response = stringEntityExchangeResult.getResponseBody();
                   assert Objects.nonNull(response);
                });
    }
    @Test
    void updateMovieInfo() {
        var newMovieInfo = new MovieInfo("aac3", "Once Upon a Time in Hollywood",
                2019, List.of("Leonardo DiCaprio, Al Pacino",
                "Brad Pitt", "Margot Robbie", "Luke Perry"), LocalDate.parse("2019-07-26"));
        when(movieInfoServiceMock.addMovieInfo(newMovieInfo)).thenReturn(Mono.just(newMovieInfo));
        webTestClient
                .post()
                .uri(MOVIE_INFO_URI)
                .bodyValue(newMovieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.nonNull(savedMovieInfo);
                    assert Objects.equals(savedMovieInfo.getId(), "aac3");
                });
        newMovieInfo.setReleaseDate(LocalDate.parse("2019-08-31"));
        when(movieInfoServiceMock.updateMovieInfo(newMovieInfo, "aac3")).thenReturn(Mono.just(newMovieInfo));
        webTestClient
                .put()
                .uri(MOVIE_INFO_URI + "/" + newMovieInfo.getId())
                .bodyValue(newMovieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.nonNull(savedMovieInfo);
                    assert Objects.equals(savedMovieInfo.getName(), "Once Upon a Time in Hollywood");
                    assert Objects.equals(savedMovieInfo.getReleaseDate(), LocalDate.parse("2019-08-31"));
                });
    }
    @Test
    void deleteMovieInfo() {
        var movieInfo = new MovieInfo("sg5", "Batman Begins", 2005, List.of("Christian Bale",
                        "Michael Cane"), LocalDate.parse("2005-06-15"));
        String movieToDeleteId = "sg5";
        when(movieInfoServiceMock.addMovieInfo(movieInfo)).thenReturn(Mono.just(movieInfo));
        webTestClient
                .post()
                .uri(MOVIE_INFO_URI)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfos = movieInfoEntityExchangeResult.getResponseBody();
                    assert Objects.nonNull(savedMovieInfos);
                    assert Objects.equals(savedMovieInfos.getName(), "Batman Begins");
                });
        when(movieInfoServiceMock.deleteMovieInfoById("sg5")).thenReturn(Mono.empty());
        webTestClient
                .delete()
                .uri(MOVIE_INFO_URI + "/" + movieToDeleteId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }
}
