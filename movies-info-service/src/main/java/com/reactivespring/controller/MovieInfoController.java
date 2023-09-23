package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
public class MovieInfoController {
    @Autowired
    private MovieInfoService movieInfoService;

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
    }
    @GetMapping("/movieinfos")
    @ResponseStatus(HttpStatus.OK)
    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoService.getAllMovieInfos().log();
    }
    @GetMapping("movieinfos/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@NonNull @PathVariable String id) {
        return movieInfoService
                .findMovieInfoById(id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
    @PutMapping("movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfoById(@RequestBody MovieInfo movieInfo, @NonNull @PathVariable String id) {
        return movieInfoService
                .updateMovieInfo(movieInfo, id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
    @DeleteMapping("movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletedMovieInfo(@NonNull @PathVariable String id) {
        return movieInfoService.deleteMovieInfoById(id);
    }
}
