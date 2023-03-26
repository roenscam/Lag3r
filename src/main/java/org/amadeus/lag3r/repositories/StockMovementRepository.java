package org.amadeus.lag3r.repositories;

import org.amadeus.lag3r.data.StockMovement;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface StockMovementRepository extends R2dbcRepository<StockMovement, String> {

     Mono<StockMovement> findById(String id);

     Flux<StockMovement> findAllByDescriptionHash( Long descriptionHash);

     Flux<StockMovement> findAllByDescriptionHashAndTargetLocationId( Long descriptionHash, Long targetLocationId );

     Flux<StockMovement> findAllByDescriptionHashAndStartLocationId( Long descriptionHash, Long startLocationId );

     Flux<StockMovement> findAllByDescriptionHashAndStartLocationIdOrTargetLocationId( Long descriptionHash, Long startLocationId,  Long targetLocationId );



}
