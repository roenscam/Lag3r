package org.amadeus.lag3r.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    private Long locationId;

    private String warehouse;

    private String location;

    private String description;
}
