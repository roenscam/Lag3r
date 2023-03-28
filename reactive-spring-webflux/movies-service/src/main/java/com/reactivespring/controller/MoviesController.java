package com.reactivespring.controller;

import com.reactivespring.client.MovieInfosRestClient;
import com.reactivespring.client.ReviewTestClient;
import com.reactivespring.domain.Movie;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private MovieInfosRestClient movieInfosRestClient;
    private ReviewTestClient reviewTestClient;

    public MoviesController(MovieInfosRestClient movieInfosRestClient, ReviewTestClient reviewTestClient) {
        this.movieInfosRestClient = movieInfosRestClient;
        this.reviewTestClient = reviewTestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> retriveMovieById(@PathVariable("id") String movieId) {
            return movieInfosRestClient.retrieveMovieInfo(movieId).flatMap(movieInfo -> {
                var reviewsListMono = reviewTestClient.retrieveReviews(movieId).collectList();
            return reviewsListMono.map(reviews -> new Movie(movieInfo, reviews));
            });
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Movie> retriveMovieInfos() {
        return movieInfosRestClient.retrieveMovieInfoStream().flatMap(movieInfo -> {
            var reviewsListMono = reviewTestClient.retrieveReviews(movieInfo.getMovieInfoId()).collectList();
            return reviewsListMono.map(reviews -> new Movie(movieInfo, reviews));
        });
    }
}
