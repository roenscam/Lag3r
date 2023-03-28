package com.reactivespring.client;

import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class MovieInfosRestClient {

    private WebClient webClient;


    @Value("${restClient.movieInfoUrl}")
    private String movieInfoUrl;

    public MovieInfosRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo (String movieId) {

        var url = movieInfoUrl.concat("/{id}");
        return webClient.get().uri(url, movieId)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(
                                new MoviesInfoClientException("There is no MovieInfo with id =" + movieId,
                                        clientResponse.statusCode().value()));
                    }
                    return clientResponse.bodyToMono(String.class).flatMap(message ->
                        Mono.error(new MoviesInfoClientException(message, clientResponse.statusCode().value()))
                    );
                    })

                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> clientResponse.bodyToMono(String.class).flatMap(message ->
                                    Mono.error(new MoviesInfoServerException(message))))

                .bodyToMono(MovieInfo.class)
                .retryWhen(RetryUtil.retrySpec())
                .log();

    }

    public Flux<MovieInfo> retrieveMovieInfoStream() {
        var url = this.movieInfoUrl + "/stream";

        return webClient.get().uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> {
                            return clientResponse.bodyToMono(String.class).flatMap(message ->
                                    Mono.error(new MoviesInfoClientException(message, clientResponse.statusCode().value()))
                            );
                        })
                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> clientResponse.bodyToMono(String.class).flatMap(message ->
                                Mono.error(new MoviesInfoServerException(message))))

                .bodyToFlux(MovieInfo.class)
                .retryWhen(RetryUtil.retrySpec())
                .log();



    }
}
