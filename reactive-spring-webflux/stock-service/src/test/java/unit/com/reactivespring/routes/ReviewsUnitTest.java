package com.reactivespring.routes;

import com.reactivespring.domain.StockMovement;
import com.reactivespring.domain.StockMovementStatus;
import com.reactivespring.domain.StockMovementType;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.StockMovementHandler;
import com.reactivespring.repository.StockMovementReactiveRepository;
import com.reactivespring.router.StockMovementRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration; // Läd sich so die Beans aus dem Kontext
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {StockMovementRouter.class, StockMovementHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private StockMovementReactiveRepository stockMovementReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReview() {

        var stockMovement =  new StockMovement(null, 3L, "1", null, "1", StockMovementStatus.COMPLETED, StockMovementType.STORE_STOCK,null);


        when(stockMovementReactiveRepository.save(isA(StockMovement.class)))
                .thenReturn(Mono.just(new StockMovement("123", 3L, "1", null, "1", StockMovementStatus.COMPLETED, StockMovementType.STORE_STOCK,null)));

        webTestClient.post().uri("/v1/stockMovement").bodyValue(stockMovement)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(StockMovement.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savStockMovement1 = reviewEntityExchangeResult.getResponseBody();
                    assert savStockMovement1 != null;
                    assert savStockMovement1.getStockMovementId() != null;
                });
    }

}
