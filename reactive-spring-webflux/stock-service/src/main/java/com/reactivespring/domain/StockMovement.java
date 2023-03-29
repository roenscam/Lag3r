package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class StockMovement {

    @Id
    private String stockMovementId;

    @Min (value = 1L, message = "StockMovement.quantity :  please pass a value higher than 1")
    private Long quantity;

    @NotEmpty (message = "StockMovement.partDescriptionId : please pass a value")
    private String partDescriptionId;

    private String startLocationId;

    private String targetLocationId;

    private StockMovementStatus status;

    private  StockMovementType type;

    private String comment;
}
