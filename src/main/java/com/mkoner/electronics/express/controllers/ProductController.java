package com.mkoner.electronics.express.controllers;

import com.mkoner.electronics.express.entity.Product;
import com.mkoner.electronics.express.exceptions.CategoryNotFoundException;
import com.mkoner.electronics.express.exceptions.ProductNotFoundException;
import com.mkoner.electronics.express.params.CreateProductParams;
import com.mkoner.electronics.express.params.GetProductFilters;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Product> createProduct(@RequestPart("product") CreateProductParams createProductParams, @RequestPart("files") MultipartFile[] files)
            throws Exception {
        LOGGER.info("createProduct called from product controller");
        createProductParams.setFiles(files);
        return new ResponseEntity<>(productService.createProduct(createProductParams), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Product> getAllProducts(){
        return productService.getAllProduct();
    }

    @GetMapping("")
    public Page<Product> getAllProductsFilter(ProductPage productPage, GetProductFilters getProductFilters){
        return productService.getProductsFilter(productPage, getProductFilters);
    }

    @GetMapping("/id/{id}")
    public Product getProductById(@PathVariable("id") Long id) throws ProductNotFoundException {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable("id") Long id, @RequestBody CreateProductParams createProductParams) throws ProductNotFoundException, CategoryNotFoundException {
        return productService.updateProduct(id, createProductParams);
    }

    @DeleteMapping("/{id}")
    public Void deleteProduct(@PathVariable("id") Long id) throws ProductNotFoundException {
        return productService.deleteProduct(id);
    }

    @PostMapping("/add_files/{id}")
    public  Void addFiles(@PathVariable("id") Long productId, @RequestParam("files") MultipartFile[] files) throws Exception {
        productService.addFiles(productId, files);
        return null;
    }

    @DeleteMapping("delete_file/{productId}/{fileId}")
    public Void removeFile(@PathVariable("productId") Long productId, @PathVariable("fileId") String fileId) throws Exception {
        productService.removeFile(productId,fileId);
        return null;
    }
}