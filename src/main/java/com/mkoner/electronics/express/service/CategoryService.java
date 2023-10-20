package com.mkoner.electronics.express.service;

import com.mkoner.electronics.express.entity.Category;
import com.mkoner.electronics.express.exceptions.CategoryNotFoundException;
import com.mkoner.electronics.express.params.CreateCategoryParams;
import com.mkoner.electronics.express.params.GetCategoryFilters;
import com.mkoner.electronics.express.params.ProductPage;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    Category createCategory(CreateCategoryParams createCategoryParams) throws Exception;
    List<Category> findAllCategories();
    Page<Category>  getCategoriesFilter(ProductPage productPage, GetCategoryFilters getCategoryFilters);
    Category updateCategory(Long categoryId, Category category) throws CategoryNotFoundException;
    Category findCategoryById(Long categoryId) throws CategoryNotFoundException;
    Category findCategoryByName(String categoryName);
    Void deleteCategory(Long id) throws CategoryNotFoundException;

    Category addFile(Long categoryId, MultipartFile file) throws Exception;
    Category removeFile(Long categoryId, String fileId) throws Exception;
}
