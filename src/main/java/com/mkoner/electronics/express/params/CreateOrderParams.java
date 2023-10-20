package com.mkoner.electronics.express.params;

import com.mkoner.electronics.express.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderParams {
    private Long customerId;
    private List<Long> lineItemIds;
    private Address address;
    private String orderStatus;
}
