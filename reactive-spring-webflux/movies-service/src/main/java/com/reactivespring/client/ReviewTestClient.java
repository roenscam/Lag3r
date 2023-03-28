package com.reactivespring.client;

import com.reactivespring.domain.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Configuration
public class ReviewTestClient {

    private WebClient webClient;

    @Value("${restClient.reviewUrl}")
    private String reviewUrl;


    public ReviewTestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Review> retrieveReviews(String movieId) {
        var url = UriComponentsBuilder.fromHttpUrl(reviewUrl).queryParam("movieInfoId",movieId)
                .buildAndExpand().toUriString();
        return webClient.get().uri(url).retrieve().bodyToFlux(Review.class);
    }
}
