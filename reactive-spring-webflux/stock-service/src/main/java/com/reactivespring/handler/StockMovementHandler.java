package com.reactivespring.handler;


import com.reactivespring.domain.StockMovement;
import com.reactivespring.exception.StockMovementDataException;
import com.reactivespring.repository.StockMovementReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

/**
 * Mit der @Component annotation, es wird als Bean erkannt und kann automatisch in andere Bean Klassen injected werden.
 * Siehe StockMovementRouter, injection über konstruktor.
 */
@Component
@Slf4j
public class StockMovementHandler {

    @Autowired
    private Validator validator;

    private StockMovementReactiveRepository stockMovementReactiveRepository;

    private Sinks.Many<StockMovement> stockMovementManySink = Sinks.many().replay().all();

    public StockMovementHandler(StockMovementReactiveRepository stockMovementReactiveRepository) {
        this.stockMovementReactiveRepository = stockMovementReactiveRepository;
    }

    public Mono<ServerResponse> addStockMovements(ServerRequest request) {
        // Wenn man eine reactive Operation auf eine reactiv Object ausführt muss man flatmap benutzen.
       return  request.bodyToMono(StockMovement.class)
                .doOnNext(this::validate)
                .flatMap(stockMovementReactiveRepository::save)
                .doOnNext(StockMovement -> this.stockMovementManySink.tryEmitNext(StockMovement))
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);


    }

    private void validate(StockMovement StockMovement) {
        var constraintViolations = validator.validate(StockMovement);

        log.info("constraintViolations: {}", constraintViolations);
        if (constraintViolations.size() > 0) {
            var errorMessage = constraintViolations.stream().map(ConstraintViolation::getMessage)
                    .sorted().collect(Collectors.joining(","));

            throw new StockMovementDataException(errorMessage);
        }

    }

    public Mono<ServerResponse> getStockMovements(ServerRequest request) {
        var fluxStockMovement = stockMovementReactiveRepository.findAll();
        return ServerResponse.ok().body(fluxStockMovement, StockMovement.class);
    }

    public Mono<ServerResponse> updateStockMovement(ServerRequest request) {

        var stockMovementId = request.pathVariable("id");
        var existingStockMovement = stockMovementReactiveRepository.findById(stockMovementId);
               // .switchIfEmpty(Mono.error(new StockMovementNotFoundException("StockMovement not found for the given id " + stockMovementId)));
       return existingStockMovement.flatMap(stockMovement -> request.bodyToMono(StockMovement.class)
                 .map(requestStockMovement -> {
                     stockMovement.setComment(requestStockMovement.getComment());
                     return stockMovement;
                 })
                 .flatMap(stockMovementReactiveRepository::save)
                 .flatMap(ServerResponse.ok()::bodyValue))
               .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteStockMovement(ServerRequest request) {
        var stockMovementId = request.pathVariable("id");
        var existingStockMovement = stockMovementReactiveRepository.findById(stockMovementId);

        return existingStockMovement.flatMap(StockMovement-> stockMovementReactiveRepository.deleteById(stockMovementId))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getStockMovementsById(ServerRequest request) {
        var stockMovementId = request.pathVariable("stockMovementId");

        var existingStockMovements = stockMovementReactiveRepository.findByStockMovementId(stockMovementId);
        return ServerResponse.ok().body(existingStockMovements, StockMovement.class);

    }

    public Mono<ServerResponse> getStockMovementsStream(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(stockMovementManySink.asFlux(), StockMovement.class).log();
    }
}
