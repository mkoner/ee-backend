package com.mkoner.electronics.express.serviceImpl;

import com.mkoner.electronics.express.entity.Category;
import com.mkoner.electronics.express.entity.File;
import com.mkoner.electronics.express.entity.Product;
import com.mkoner.electronics.express.constants.ExceptionMessages;
import com.mkoner.electronics.express.exceptions.CategoryNotFoundException;
import com.mkoner.electronics.express.exceptions.ProductNotFoundException;
import com.mkoner.electronics.express.params.CreateProductParams;
import com.mkoner.electronics.express.params.GetProductFilters;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.repository.FileRepository;
import com.mkoner.electronics.express.repository.ProductCriteriaRepository;
import com.mkoner.electronics.express.repository.ProductRepository;
import com.mkoner.electronics.express.service.CategoryService;
import com.mkoner.electronics.express.service.FileService;
import com.mkoner.electronics.express.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductCriteriaRepository productCriteriaRepository;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileService fileService;

    @Autowired
    FileRepository fileRepository;

    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    @CacheEvict(value = {"all-products", "products"}, allEntries = true)
    public Product createProduct(CreateProductParams createProductParams) throws Exception {
        LOGGER.info("Called createProduct service impl");
        Product product = new Product();
        List<File> files = fileService.saveFiles(createProductParams.getFiles());
        Category category = categoryService.findCategoryById(createProductParams.getCategoryId());
        product.setCategory(category);
        product.setProductPrice(createProductParams.getProductPrice());
        product.setProductName(createProductParams.getProductName());
        product.setProductDescription(createProductParams.getProductDescription());
        product.setFiles(files);
        /*
        for(File file: files){
            fileRepository.save(file);
        }
         */
        return productRepository.save(product);
    }

    @Override
    @Cacheable(value = "all-products")
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    @Override
    @Cacheable(value = "products")
    public Page<Product> getProductsFilter(ProductPage productPage, GetProductFilters getProductFilters) {
        if(Objects.isNull(productPage.getSortBy()) || productPage.getSortBy().isBlank()){
            productPage.setSortBy("productId");
        }
        return productCriteriaRepository.findAllFiltered(productPage, getProductFilters);
    }

    @Override
    public Product getProductById(Long id) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty())
            throw new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND);
        return product.get();
    }

    @Override
    @CachePut(value = {"all-products", "products"})
    public Product updateProduct(Long id, CreateProductParams createProductParams) throws ProductNotFoundException, CategoryNotFoundException {
        Product productToUpdate = getProductById(id);
        Category category = null;
        if(Objects.nonNull(createProductParams.getProductName()) && !createProductParams.getProductName().isBlank())
            productToUpdate.setProductName(createProductParams.getProductName());
        if(Objects.nonNull(createProductParams.getProductPrice()))
            productToUpdate.setProductPrice(createProductParams.getProductPrice());
        if(Objects.nonNull((createProductParams.getProductDescription())) && !createProductParams.getProductDescription().isBlank())
            productToUpdate.setProductDescription(createProductParams.getProductDescription());
        if(Objects.nonNull(createProductParams.getCategoryId())){
            category = categoryService.findCategoryById(createProductParams.getCategoryId());
            productToUpdate.setCategory(category);
        }
        productToUpdate.setModifiedAt(new Date());
        return productRepository.save(productToUpdate);
    }

    @Override
    @CacheEvict(value = {"all-products", "products"}, allEntries = true)
    public Void deleteProduct(Long id) throws ProductNotFoundException {
        getProductById(id);
        productRepository.deleteById(id);
        return null;
    }

    @Override
    @CacheEvict(value = {"all-products", "products"}, allEntries = true)
    public Product addFiles(Long productId, MultipartFile[] files) throws Exception {
        Product product = getProductById(productId);
        List<File> filesToAdd = fileService.saveFiles(files);
        product.addFile(filesToAdd);
        for(File file: filesToAdd){
            fileRepository.save(file);
        }
        product.setModifiedAt(new Date());
        productRepository.save(product);
        return null;
    }

    @Override
    @CacheEvict(value = {"all-products", "products"}, allEntries = true)
    public Product removeFile(Long productId, String fileId) throws Exception {
        Product product = getProductById(productId);
        File file = fileService.getFileById(fileId);
        product.removeFile(file);
        fileService.deleteFile(fileId);
        product.setModifiedAt(new Date());
        productRepository.save(product);
        return null;
    }
}
