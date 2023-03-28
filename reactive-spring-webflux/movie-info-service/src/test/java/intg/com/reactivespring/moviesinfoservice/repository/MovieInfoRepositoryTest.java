package com.reactivespring.moviesinfoservice.repository;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

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
    void findAll() {
        var moviesInfoFlux = movieInfoRepository.findAll();
        StepVerifier.create(moviesInfoFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById() {
        var moviesInfoMono = movieInfoRepository.findById("abc");
        StepVerifier.create(moviesInfoMono).assertNext(movieInfo -> {
            assertEquals("Batman Begins",movieInfo.getName());
        }).verifyComplete();
    }

    @Test
    void saveMovieInfo() {
        var info = new MovieInfo( null, "The Light Knight",2008, List.of("Christian Bale", "Heath Ledger"),LocalDate.parse("2008-07-18"));

        var moviesInfoMono = movieInfoRepository.save(info).log();

        StepVerifier.create(moviesInfoMono).assertNext(movieInfo -> {
            assertEquals("The Light Knight", movieInfo.getName());
        }).verifyComplete();
    }

    @Test
    void updateMovieInfo() {
        var moviesInfo = movieInfoRepository.findById("abc").block();
        moviesInfo.setYear(1999);

        var moviesInfoMono = movieInfoRepository.save(moviesInfo).log();

        StepVerifier.create(moviesInfoMono).assertNext(movieInfo -> {
            assertEquals(1999, movieInfo.getYear());
        }).verifyComplete();
    }

    @Test
    void findbyYear() {
        var moviesInfo = movieInfoRepository.findByYear(2005);
        StepVerifier.create(moviesInfo).assertNext(movieInfo -> {
            assertEquals(2005,movieInfo.getYear());
        });
    }
}