package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class MovieInfoService {
    @Autowired
    private MovieInfoRepository movieInfoRepository;

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }
    public Mono<MovieInfo> findMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }
    public Flux<MovieInfo> getAllMovieInfos() { return movieInfoRepository.findAll(); }
    public Mono<MovieInfo> updateMovieInfo(MovieInfo movieInfo, String id) {
        return movieInfoRepository
                .findById(id)
                .flatMap(updatedMovieInfo -> {
                    updatedMovieInfo.setYear(movieInfo.getYear());
                    updatedMovieInfo.setCast(movieInfo.getCast());
                    updatedMovieInfo.setName(movieInfo.getName());
                    updatedMovieInfo.setReleaseDate(movieInfo.getReleaseDate());
                    return movieInfoRepository.save(updatedMovieInfo);
                });
    }
    public Mono<Void> deleteMovieInfoById(String id) {
        return movieInfoRepository.deleteById(id);
    }

    public Flux<MovieInfo> findMovieInfoByYear(Integer year) {
        return movieInfoRepository.findByYear(year);
    }
}
