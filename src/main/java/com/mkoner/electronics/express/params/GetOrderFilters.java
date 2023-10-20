package com.mkoner.electronics.express.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetOrderFilters {
    private Long orderId;
    private String status;
    private Double maxPrice;
    private Double minPrice;
    private String customerName;
    private String customerNumber;
    private String city;

    @Override
    public String toString(){
        return "CreateOrderParams [categoryId=" + orderId + ", status=" + status +
                ", maxPrice=" + maxPrice + ", minPrice=" + minPrice + ", customerName=" + customerName + "," +
                " customerName=" + customerName + ", + customerNumber=" + customerNumber + ", + city=" + city + "]";
    }
}
