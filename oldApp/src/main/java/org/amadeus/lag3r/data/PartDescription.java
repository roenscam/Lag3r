package org.amadeus.lag3r.data;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartDescription {

    @Id
    private Long descriptionHash;

    private Long partId;

    private String partNumber;

    private String partDescription;

    private JSONPObject extraAttributes;


}
