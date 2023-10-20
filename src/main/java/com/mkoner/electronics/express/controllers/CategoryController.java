package com.mkoner.electronics.express.controllers;

import com.mkoner.electronics.express.entity.Category;
import com.mkoner.electronics.express.exceptions.CategoryNotFoundException;
import com.mkoner.electronics.express.params.CreateCategoryParams;
import com.mkoner.electronics.express.params.GetCategoryFilters;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.service.CategoryService;
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
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    private final Logger LOGGER = LoggerFactory.getLogger(Category.class);

    @PostMapping(value = "", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Category> createCategory(@RequestPart("category") CreateCategoryParams createCategoryParams, @RequestPart("file") MultipartFile file)
            throws Exception{
        createCategoryParams.setFile(file);
        LOGGER.info((createCategoryParams.toString()));
        return new ResponseEntity<>(categoryService.createCategory(createCategoryParams), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public List<Category> findAllCategories(){
        return categoryService.findAllCategories();
    }

    @GetMapping("")
    public Page<Category> getCategoriesFilter(ProductPage productPage, GetCategoryFilters getCategoryFilters){
        return categoryService.getCategoriesFilter(productPage, getCategoryFilters);
    }

    @GetMapping("/id/{id}")
    public Category findCategoryById(@PathVariable("id") Long id) throws CategoryNotFoundException {
        return categoryService.findCategoryById(id);
    }

    @GetMapping("/name/{categoryName}")
    public Category findCategoryByName(@PathVariable("categoryName") String categoryName){
        return categoryService.findCategoryByName(categoryName);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable("id") Long categoryId, @RequestBody Category category) throws CategoryNotFoundException {
        return categoryService.updateCategory(categoryId, category);
    }

    @DeleteMapping("/{id}")
    public Void deleteCategory(@PathVariable("id") Long categoryId) throws CategoryNotFoundException {
        return categoryService.deleteCategory(categoryId);
    }


    @PutMapping("/add_files/{id}")
    public  Void addFiles(@PathVariable("id") Long categoryId, @RequestParam("file") MultipartFile file) throws Exception {
        categoryService.addFile(categoryId, file);
        return null;
    }

    @DeleteMapping("delete_file/{categoryId}/{fileId}")
    public Void removeFile(@PathVariable("categoryId") Long categoryId, @PathVariable("fileId") String fileId) throws Exception {
        categoryService.removeFile(categoryId, fileId);
        return null;
    }
}
