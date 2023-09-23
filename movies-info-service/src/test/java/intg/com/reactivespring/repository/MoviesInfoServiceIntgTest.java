package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;


@DataMongoTest
@ActiveProfiles("test")
public class MoviesInfoServiceIntgTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;

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

    @Test
    void findAll() {
        // given
        // when
        Flux<MovieInfo> allMovies = movieInfoRepository.findAll().log();
        // then
        StepVerifier.create(allMovies)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findMovieById() {
        Mono<MovieInfo> movie = movieInfoRepository.findById("se7").log();
        StepVerifier
                .create(movie)
                .assertNext(movieInfo -> {
                    assert movieInfo.getName().equalsIgnoreCase("pulp fiction");
                })
                .verifyComplete();
    }

    @Test
    void saveMovie() {
        var movie = new MovieInfo(null, "Inglorious Bastards", 2005,
                List.of("Brad Pitt", "Christoph Waltz", "Eli Roth", "Diane Kruger"), LocalDate.parse("2009-09-17"));
        var monoMovieInfo = movieInfoRepository.save(movie);

        StepVerifier
                .create(monoMovieInfo)
                .assertNext(movieInfo -> {
                    assert movieInfo.getName().equalsIgnoreCase("inglorious bastards");
                })
                .verifyComplete();
    }
    @Test
    void updateMovie() {
        // given
        var movie = movieInfoRepository.findById("se7").block();
        movie.setYear(2024);
        // when
        var monoMovieInfo = movieInfoRepository.save(movie);

        StepVerifier
                .create(monoMovieInfo)
                .assertNext(movieInfo -> {
                    assert movieInfo.getYear().equals(2024);
                })
                .verifyComplete();
    }
    @Test
    void deleteMovie() {
        movieInfoRepository.deleteById("sg5").block();
        Flux<MovieInfo> allMovies = movieInfoRepository.findAll().log();
        StepVerifier.create(allMovies)
                .expectNextCount(2)
                .verifyComplete();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }
}
