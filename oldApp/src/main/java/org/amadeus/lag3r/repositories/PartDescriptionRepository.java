package org.amadeus.lag3r.repositories;

import org.amadeus.lag3r.data.PartDescription;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PartDescriptionRepository extends R2dbcRepository<PartDescription, Long> {
}
