package com.mkoner.electronics.express.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProductFilters {
    private Long categoryId;
    private String productName;
    private Double maxPrice;
    private Double minPrice;
    private String productDescription;

    @Override
    public String toString(){
        return "CreateProductParams [categoryId=" + categoryId + ", productName=" + productName +
                ", maxPrice=" + maxPrice + ", minPrice=" + minPrice + ", productDescription=" + productDescription + "]";
    }
}
