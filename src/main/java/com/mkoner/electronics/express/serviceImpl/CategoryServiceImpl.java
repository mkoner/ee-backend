package com.mkoner.electronics.express.serviceImpl;

import com.mkoner.electronics.express.entity.Category;
import com.mkoner.electronics.express.constants.ExceptionMessages;
import com.mkoner.electronics.express.entity.File;
import com.mkoner.electronics.express.entity.Product;
import com.mkoner.electronics.express.exceptions.CategoryNotFoundException;
import com.mkoner.electronics.express.exceptions.FileNotFoundException;
import com.mkoner.electronics.express.params.CreateCategoryParams;
import com.mkoner.electronics.express.params.GetCategoryFilters;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.repository.CategoryCriteriaRepository;
import com.mkoner.electronics.express.repository.CategoryRepository;
import com.mkoner.electronics.express.service.CategoryService;
import com.mkoner.electronics.express.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryCriteriaRepository categoryCriteriaRepository;

    @Autowired
    private FileService fileService;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    @CacheEvict(value = {"categories", "all-categories"}, allEntries = true)
    public Category createCategory(CreateCategoryParams createCategoryParams) throws Exception {
        Category category = new Category();
        File file = fileService.saveFile(createCategoryParams.getFile());
        category.setFile(file);
        category.setCategoryName(createCategoryParams.getCategoryName());
        category.setCategoryOrder(createCategoryParams.getCategoryOrder());
        return categoryRepository.save(category);
    }

    @Override
    @Cacheable(value = "all-categories")
    public List<Category> findAllCategories() {
        LOGGER.info("FROM DB findAllCategories");
        return categoryRepository.findAll();
    }

    @Override
    @Cacheable(value = "categories")
    public Page<Category> getCategoriesFilter(ProductPage productPage, GetCategoryFilters getCategoryFilters) {
        if(Objects.isNull(productPage.getSortBy()) ||  productPage.getSortBy().isBlank()){
            productPage.setSortBy("categoryId");
        }
        LOGGER.info("FROM DB getCategoriesFilter");
        return categoryCriteriaRepository.findAllFiltered(productPage, getCategoryFilters);
    }

    @Override
    @CacheEvict(value = {"categories", "all-categories"}, allEntries = true)
    public Category updateCategory(Long categoryId, Category category) throws CategoryNotFoundException {
        Optional<Category> category1 = categoryRepository.findById(categoryId);
        if(category1.isEmpty())
            throw new CategoryNotFoundException(ExceptionMessages.CATEGORY_NOT_FOUND);
        Category categoryToUpdate = category1.get();
        if(Objects.nonNull(category.getCategoryName()) && !category.getCategoryName().isBlank())
            categoryToUpdate.setCategoryName(category.getCategoryName());
        if(Objects.nonNull(category.getCategoryOrder()))
            categoryToUpdate.setCategoryOrder(category.getCategoryOrder());
        categoryToUpdate.setModifiedAt(new Date());
        return categoryRepository.save(categoryToUpdate);
    }

    @Override
    public Category findCategoryById(Long categoryId) throws CategoryNotFoundException {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if(category.isEmpty())
            throw new CategoryNotFoundException(ExceptionMessages.CATEGORY_NOT_FOUND);
        return category.get();
    }

    @Override
    public Category findCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryNameIgnoreCase(categoryName);
    }


    @Override
    @CacheEvict(value = {"categories", "all-categories"}, allEntries = true)
    public Void deleteCategory(Long id) throws CategoryNotFoundException {
        findCategoryById(id);
        categoryRepository.deleteById(id);
        return null;
    }

    @Override
    @CacheEvict(value = {"categories", "all-categories"}, allEntries = true)
    public Category addFile(Long categoryId, MultipartFile file) throws Exception {
        Category category = findCategoryById(categoryId);
        File file1 = fileService.saveFile(file);
        category.setFile(file1);
        category.setModifiedAt(new Date());
        categoryRepository.save(category);
        return null;
    }

    @Override
    @CacheEvict(value = {"categories", "all-categories"}, allEntries = true)
    public Category removeFile(Long categoryId, String fileId) throws CategoryNotFoundException, FileNotFoundException {
        Category category = findCategoryById(categoryId);
        category.setFile(null);
        fileService.deleteFile(fileId);
        category.setModifiedAt(new Date());
        categoryRepository.save(category);
        return null;
    }
}
