package com.mkoner.electronics.express.params;

import com.mkoner.electronics.express.entity.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductParams {
    private Long categoryId;
    private String productName;
    private Double productPrice;
    private String productDescription;
    private MultipartFile[] files;
}
