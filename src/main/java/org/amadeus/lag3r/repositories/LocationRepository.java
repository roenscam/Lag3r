package org.amadeus.lag3r.repositories;

import org.amadeus.lag3r.data.Location;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends R2dbcRepository<Location, Long> {
}
