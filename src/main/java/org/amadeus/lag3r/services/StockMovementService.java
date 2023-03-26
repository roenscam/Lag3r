package org.amadeus.lag3r.services;

import org.amadeus.lag3r.data.StockMovement;
import org.amadeus.lag3r.repositories.StockMovementRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StockMovementService {

    StockMovementRepository stockMovementRepository;

    StockMovementService (StockMovementRepository pStockMovementRepository) {
        this.stockMovementRepository = pStockMovementRepository;
    }

    public Mono<StockMovement> findById (String id) {
        return this.stockMovementRepository.findById(id);
    }


}
