package com.mkoner.electronics.express.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryFilters {

    private Long categoryId;
    private String categoryName;
    private int categoryOrder;

    @Override
    public String toString(){
        return "CreateCategoryParams [categoryId=" + categoryId + ", categoryName=" + categoryName +
                ", categoryOrder=" + categoryOrder + "]";
    }
}
