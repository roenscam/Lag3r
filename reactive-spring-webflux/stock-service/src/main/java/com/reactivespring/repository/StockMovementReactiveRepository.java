package com.reactivespring.repository;

import com.reactivespring.domain.StockMovement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface StockMovementReactiveRepository extends ReactiveMongoRepository<StockMovement, String> {


    Flux<StockMovement> findByStockMovementId(String stockMovementId);
}
