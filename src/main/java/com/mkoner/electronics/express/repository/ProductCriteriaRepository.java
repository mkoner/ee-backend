package com.mkoner.electronics.express.repository;

import com.mkoner.electronics.express.entity.Product;
import com.mkoner.electronics.express.params.CreateProductParams;
import com.mkoner.electronics.express.params.GetProductFilters;
import com.mkoner.electronics.express.params.ProductPage;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ProductCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public ProductCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Product> findAllFiltered(ProductPage productPage, GetProductFilters getProductFilters){
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> productRoot = criteriaQuery.from(Product.class);
        Predicate predicate = getPredicate(getProductFilters, productRoot);
        criteriaQuery.where(predicate);
        setOrder(productPage, criteriaQuery, productRoot);

        TypedQuery<Product> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(productPage.getCurrentPage() * productPage.getPageSize());
        typedQuery.setMaxResults(productPage.getPageSize());

        Pageable pageable = getPageable(productPage);

        long usersCount = getProductsCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, usersCount);
    }

    private Predicate getPredicate(GetProductFilters getProductFilters, Root<Product> productRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(getProductFilters.getCategoryId())){
            predicates.add(
                    criteriaBuilder.equal(productRoot.get("category").get("categoryId"),
                            getProductFilters.getCategoryId())
            );
        }
        if(Objects.nonNull(getProductFilters.getProductName()) && !getProductFilters.getProductName().isBlank()){
            predicates.add(
                    criteriaBuilder.like(productRoot.get("productName"),
                            "%" + getProductFilters.getProductName()+ "%")
            );
        }
        if(Objects.nonNull(getProductFilters.getMaxPrice()) && Objects.nonNull((getProductFilters.getMinPrice()))){
            predicates.add(
                    criteriaBuilder.between(productRoot.get("productPrice"),
                            getProductFilters.getMinPrice(), getProductFilters.getMaxPrice())
            );
        }

        if(Objects.nonNull(getProductFilters.getMaxPrice()) && !Objects.nonNull((getProductFilters.getMinPrice()))){
            predicates.add(
                    criteriaBuilder.le(productRoot.get("productPrice"),
                            getProductFilters.getMaxPrice())
            );
        }

        if(!Objects.nonNull(getProductFilters.getMaxPrice()) && Objects.nonNull((getProductFilters.getMinPrice()))){
            predicates.add(
                    criteriaBuilder.ge(productRoot.get("productPrice"),
                            getProductFilters.getMinPrice())
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(ProductPage productPage,
                          CriteriaQuery<Product> criteriaQuery,
                          Root<Product> userRoot) {
        if(productPage.getSortDirection().equals(Sort.Direction.ASC)){
            criteriaQuery.orderBy(criteriaBuilder.asc(userRoot.get(productPage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(userRoot.get(productPage.getSortBy())));
        }
    }

    private Pageable getPageable(ProductPage productPage) {
        Sort sort = Sort.by(productPage.getSortDirection(), productPage.getSortBy());
        return PageRequest.of(productPage.getCurrentPage(),productPage.getPageSize(), sort);
    }

    private long getProductsCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Product> countRoot = countQuery.from(Product.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

}
