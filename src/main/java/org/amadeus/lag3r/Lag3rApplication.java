package org.amadeus.lag3r;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories
@SpringBootApplication
public class Lag3rApplication {

    public static void main(String[] args) {
        SpringApplication.run(Lag3rApplication.class, args);
    }

}
