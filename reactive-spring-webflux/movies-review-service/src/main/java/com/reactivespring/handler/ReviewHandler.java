package com.reactivespring.handler;


import com.mongodb.internal.connection.Server;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
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
 * Siehe ReviewRouter, injection über konstruktor.
 */
@Component
@Slf4j
public class ReviewHandler {

    @Autowired
    private Validator validator;

    private ReviewReactiveRepository reviewReactiveRepository;

    private Sinks.Many<Review> reviewManySink = Sinks.many().replay().all();

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReviews(ServerRequest request) {
        // Wenn man eine reactive Operation auf eine reactiv Object ausführt muss man flatmap benutzen.
       return  request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .doOnNext(review -> this.reviewManySink.tryEmitNext(review))
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);


    }

    private void validate(Review review) {
        var constraintViolations = validator.validate(review);

        log.info("constraintViolations: {}", constraintViolations);
        if (constraintViolations.size() > 0) {
            var errorMessage = constraintViolations.stream().map(ConstraintViolation::getMessage)
                    .sorted().collect(Collectors.joining(","));

            throw new ReviewDataException(errorMessage);
        }

    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        var fluxReview = reviewReactiveRepository.findAll();
        return ServerResponse.ok().body(fluxReview, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {

        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);
               // .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given id " + reviewId)));
       return existingReview.flatMap(review -> request.bodyToMono(Review.class)
                 .map(requestReview -> {
                     review.setComment(requestReview.getComment());
                     review.setRating(requestReview.getRating());
                     return review;
                 })
                 .flatMap(reviewReactiveRepository::save)
                 .flatMap(ServerResponse.ok()::bodyValue))
               .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        var reviewId = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(reviewId);

        return existingReview.flatMap(review-> reviewReactiveRepository.deleteById(reviewId))
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getReviewsByMovieId(ServerRequest request) {
        var movieId = request.pathVariable("movieId");

        var existingReviews = reviewReactiveRepository.findByMovieInfoId(movieId);
        return ServerResponse.ok().body(existingReviews, Review.class);

    }

    public Mono<ServerResponse> getReviewsStream(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewManySink.asFlux(), Review.class).log();
    }
}
