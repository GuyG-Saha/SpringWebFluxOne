package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
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
public class MovieInfoRepositoryIntgTest {
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
    void findByYear() {
        Flux<MovieInfo> movie = movieInfoRepository.findByYear(1972).log();
        StepVerifier.create(movie)
                .expectNextCount(1)
                .verifyComplete();;
    }
}
