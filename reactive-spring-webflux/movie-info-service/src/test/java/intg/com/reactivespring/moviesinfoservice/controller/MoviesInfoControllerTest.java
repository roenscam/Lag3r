package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class MoviesInfoControllerTest {
    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    private static String URI= "/v1/movieinfos";

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo("abc", "Batman Begins",2005, List.of("Christian Bale","Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo( null, "The Dark Knight",2008, List.of("Christian Bale", "Heath Ledger"),LocalDate.parse("2008-07-18")),
                new MovieInfo(null, "Dark Knight Rises",2012, List.of("Christian Bale","Tom Hardy"),LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
                .blockLast();  // <- da alle Aufrufe asynchron und gleichzeitig sind, könnte es passieren
        // dass die Tests gestartet werden bevor alle Daten eingespielt sind. Block last, waretet bis letztes Transaktion durch ist.
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll();
    }

    @Test
    void addMovieInfo() {

        var bodyValue = new MovieInfo( null, "The Dark Knight",2012, List.of("Christian Bale", "Heath Ledger"),LocalDate.parse("2008-07-18"));

        webTestClient.post().uri(URI).bodyValue(bodyValue)
                .exchange().expectStatus().is2xxSuccessful().expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    assert(movieInfoEntityExchangeResult.getResponseBody().getMovieInfoId() != null);
                });
    }

    @Test
    void getAllMovieInfos() {
        var uri = UriComponentsBuilder.fromUriString(URI)
                .queryParam("year",2005).buildAndExpand().toUri();

        webTestClient.get().uri(uri).exchange().expectStatus().is2xxSuccessful().expectBodyList(MovieInfo.class)
                .hasSize(1);

    }

    @Test
    void getAllMovieInfosStream() {

        var bodyValue = new MovieInfo( "twar", "The Dark Knight",2012, List.of("Christian Bale", "Heath Ledger"),LocalDate.parse("2008-07-18"));

        webTestClient.post().uri(URI).bodyValue(bodyValue)
                .exchange().expectStatus().is2xxSuccessful().expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    assert(movieInfoEntityExchangeResult.getResponseBody().getMovieInfoId() != null);
                });

        var uri = UriComponentsBuilder.fromUriString(URI + "/stream")
                .buildAndExpand()
                .toUri();


        var moviesStreamFlux =  webTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(MovieInfo.class)
                .getResponseBody();

        StepVerifier.create(moviesStreamFlux)
                .assertNext(movieInfo1 -> {
                    assert movieInfo1.getMovieInfoId() != null;
                })
                .thenCancel()
                .verify();
    }
}