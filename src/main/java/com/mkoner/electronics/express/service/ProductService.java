package com.mkoner.electronics.express.service;

import com.mkoner.electronics.express.entity.File;
import com.mkoner.electronics.express.entity.Product;
import com.mkoner.electronics.express.exceptions.CategoryNotFoundException;
import com.mkoner.electronics.express.exceptions.FileNotFoundException;
import com.mkoner.electronics.express.exceptions.ProductNotFoundException;
import com.mkoner.electronics.express.params.CreateProductParams;
import com.mkoner.electronics.express.params.GetProductFilters;
import com.mkoner.electronics.express.params.ProductPage;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Product createProduct(CreateProductParams createProductParams) throws Exception;

    List<Product> getAllProduct();
    Page<Product> getProductsFilter(ProductPage productPage, GetProductFilters getProductFilters);
    Product getProductById(Long id) throws ProductNotFoundException;

    Product updateProduct(Long id, CreateProductParams createProductParams) throws ProductNotFoundException, CategoryNotFoundException;

    Void deleteProduct(Long id) throws ProductNotFoundException;
    Product addFiles(Long productId, MultipartFile[] files) throws Exception;

    Product removeFile(Long productId, String fileId) throws Exception;
}