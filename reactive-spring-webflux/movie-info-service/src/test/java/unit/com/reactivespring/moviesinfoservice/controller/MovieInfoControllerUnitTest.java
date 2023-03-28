package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.service.MoviesInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {

    private static String URI= "/v1/movieinfos";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MoviesInfoService moviesInfoService;

    @Test
    void getAllMovies() {

        var movieInfo = Mono.just(new MovieInfo("abc", "Batman Begins",2005, List.of("Christian Bale","Michael Cane"), LocalDate.parse("2005-06-15")));
        String abc = "abc";
        when(moviesInfoService.findById(abc)).thenReturn(movieInfo);

        webTestClient.get().uri(URI + "/{id}", abc).exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(entityExchangeResult -> {
                    assertEquals(entityExchangeResult.getResponseBody().getMovieInfoId(), "abc");
                });
    }


}
