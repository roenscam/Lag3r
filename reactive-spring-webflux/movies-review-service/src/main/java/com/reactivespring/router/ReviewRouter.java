package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    /**
     * Hier sieht man wieso functional Programming ist lightweight
     * @Bean ist notwendig, da SpringApplication bei einem scan die routen nicht configuriern kann.
     */
    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return route()
                .nest(path("/v1/reviews"), builder -> {
                    builder.POST("", request ->  reviewHandler.addReviews(request) )
                            .GET("", request -> reviewHandler.getReviews(request))
                            .PUT("/{id}", request -> reviewHandler.updateReview(request))
                            .DELETE("/{id}", request -> reviewHandler.deleteReview(request))
                            .GET("/{movieId}", request -> reviewHandler.getReviewsByMovieId(request))
                            .GET("/stream", request -> reviewHandler.getReviewsStream(request));

                })
                .GET("/v1/helloworld", ((request) -> ServerResponse.ok().bodyValue("helloworld")))
                .build();
    }
}
