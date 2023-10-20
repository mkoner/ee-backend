package com.mkoner.electronics.express.params;
import org.springframework.data.domain.Sort;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class ProductPage {
    private int currentPage = 0;
    private int pageSize = 20;
    private Sort.Direction sortDirection = Sort.Direction.DESC;
    private String sortBy;
}
