package com.reactivespring.router;

import com.reactivespring.handler.StockMovementHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class StockMovementRouter {

    /**
     * Hier sieht man wieso functional Programming ist lightweight
     * @Bean ist notwendig, da SpringApplication bei einem scan die routen nicht configuriern kann.
     */
    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(StockMovementHandler reviewHandler) {
        return route()
                .nest(path("/v1/stockMovement"), builder -> {
                    builder.POST("", request ->  reviewHandler.addStockMovements(request) )
                            .GET("", request -> reviewHandler.getStockMovements(request))
                            .PUT("/{id}", request -> reviewHandler.updateStockMovement(request))
                            .DELETE("/{id}", request -> reviewHandler.deleteStockMovement(request))
                            .GET("/{movieId}", request -> reviewHandler.getStockMovementsById(request))
                            .GET("/stream", request -> reviewHandler.getStockMovementsStream(request));

                })
                .GET("/v1/helloworld", ((request) -> ServerResponse.ok().bodyValue("helloworld")))
                .build();
    }
}
