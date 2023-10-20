package com.mkoner.electronics.express.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLineItemParams {
    private Long productId;
    private int lineItemQuantity;
}
