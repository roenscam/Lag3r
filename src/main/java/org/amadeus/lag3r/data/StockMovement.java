package org.amadeus.lag3r.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovement {

    @Id
    private String id;

    private Long quantity;

    private Long descriptionHash;

    private Long startLocationId;

    private Long targetLocationId;
}
