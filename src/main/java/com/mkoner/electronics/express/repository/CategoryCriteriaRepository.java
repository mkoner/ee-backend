package com.mkoner.electronics.express.repository;

import com.mkoner.electronics.express.entity.Category;
import com.mkoner.electronics.express.params.GetCategoryFilters;
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
public class CategoryCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CategoryCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }


    public Page<Category> findAllFiltered(ProductPage productPage, GetCategoryFilters getCategoryFilters){
        CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(Category.class);
        Root<Category> categoryRoot = criteriaQuery.from(Category.class);
        Predicate predicate = getPredicate(getCategoryFilters, categoryRoot);
        criteriaQuery.where(predicate);
        setOrder(productPage, criteriaQuery, categoryRoot);

        TypedQuery<Category> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(productPage.getCurrentPage() * productPage.getPageSize());
        typedQuery.setMaxResults(productPage.getPageSize());

        Pageable pageable = getPageable(productPage);

        long usersCount = getCategoriesCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, usersCount);
    }

    private Predicate getPredicate(GetCategoryFilters getCategoryFilters, Root<Category> categoryRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(getCategoryFilters.getCategoryId())){
            predicates.add(
                    criteriaBuilder.equal(categoryRoot.get("categoryId"),
                            getCategoryFilters.getCategoryId())
            );
        }
        if(Objects.nonNull(getCategoryFilters.getCategoryOrder())){
            predicates.add(
                    criteriaBuilder.equal(categoryRoot.get("categoryOrder"),
                            getCategoryFilters.getCategoryOrder())
            );
        }
        if(Objects.nonNull(getCategoryFilters.getCategoryName()) && !getCategoryFilters.getCategoryName().isBlank()){
            predicates.add(
                    criteriaBuilder.like(categoryRoot.get("categoryName"),
                            "%" + getCategoryFilters.getCategoryName()+ "%")
            );
        }


        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(ProductPage productPage,
                          CriteriaQuery<Category> criteriaQuery,
                          Root<Category> userRoot) {
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

    private long getCategoriesCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Category> countRoot = countQuery.from(Category.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

}
