package com.reactivespring.routes;


import com.reactivespring.domain.StockMovement;
import com.reactivespring.domain.StockMovementStatus;
import com.reactivespring.domain.StockMovementType;
import com.reactivespring.repository.StockMovementReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureWebTestClient
public class StockMovementIntgTest {


    @Autowired
    WebTestClient webTestClient;

    @Autowired
    StockMovementReactiveRepository stockMovementReactiveRepository;

    @AfterEach
    void tearDown() {
        stockMovementReactiveRepository.deleteAll().block();
    }

    static String REVIEWS_URL = "/v1/stockMovement";

    @BeforeEach
    void setUp() {
        var reviewsList = List.of(
                new StockMovement(null, 3L, "1", null, "1", StockMovementStatus.COMPLETED, StockMovementType.STORE_STOCK,null),
                new StockMovement(null, 2L, "1", "1", "2", StockMovementStatus.COMPLETED, StockMovementType.RELOCATE,null),
                new StockMovement(null, 1L, "1", "2", null, StockMovementStatus.COMPLETED, StockMovementType.OUTPUT,null)
        );
        stockMovementReactiveRepository.saveAll(reviewsList).blockLast();
    }

    @Test
    void addReview() {

       var stockMovement =  new StockMovement(null, 3L, "1", null, "1", StockMovementStatus.COMPLETED, StockMovementType.STORE_STOCK,null);

        webTestClient.post().uri(REVIEWS_URL).bodyValue(stockMovement).exchange()
               .expectStatus().isCreated()
               .expectBody(StockMovement.class).consumeWith(reviewEntityExchangeResult -> {
                   var savedReview = reviewEntityExchangeResult.getResponseBody();
                   assert savedReview != null;
                   assert savedReview.getStockMovementId() != null;
               });
    }


}
