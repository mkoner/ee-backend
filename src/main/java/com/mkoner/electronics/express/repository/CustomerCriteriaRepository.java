package com.mkoner.electronics.express.repository;


import com.mkoner.electronics.express.entity.Admin;
import com.mkoner.electronics.express.entity.Customer;
import com.mkoner.electronics.express.params.GetUserFilters;
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
public class CustomerCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public CustomerCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Customer> findAllFiltered(ProductPage productPage, GetUserFilters getUserFilters){
        CriteriaQuery<Customer> criteriaQuery = criteriaBuilder.createQuery(Customer.class);
        Root<Customer> customerRootRoot = criteriaQuery.from(Customer.class);
        Predicate predicate = getPredicate(getUserFilters, customerRootRoot);
        criteriaQuery.where(predicate);
        setOrder(productPage, criteriaQuery, customerRootRoot);

        TypedQuery<Customer> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(productPage.getCurrentPage() * productPage.getPageSize());
        typedQuery.setMaxResults(productPage.getPageSize());

        Pageable pageable = getPageable(productPage);

        long usersCount = getProductsCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, usersCount);
    }

    private Predicate getPredicate(GetUserFilters getUserFilters, Root<Customer> customerRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(getUserFilters.getUserId())){
            predicates.add(
                    criteriaBuilder.equal(customerRoot.get("customerId"),
                            getUserFilters.getUserId())
            );
        }
        if(Objects.nonNull(getUserFilters.getName()) && !getUserFilters.getName().isBlank()){
            predicates.add(
                    criteriaBuilder.like(customerRoot.get("name"),
                            "%" + getUserFilters.getName()+ "%")
            );
        }
        if(Objects.nonNull(getUserFilters.getNumber()) && !getUserFilters.getNumber().isBlank()){
            predicates.add(
                    criteriaBuilder.like(customerRoot.get("number"),
                            "%" + getUserFilters.getNumber()+ "%")
            );
        }

        if(Objects.nonNull(getUserFilters.getEmail()) && !getUserFilters.getEmail().isBlank()){
            predicates.add(
                    criteriaBuilder.like(customerRoot.get("email"),
                            "%" + getUserFilters.getEmail()+ "%")
            );
        }


        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(ProductPage productPage,
                          CriteriaQuery<Customer> criteriaQuery,
                          Root<Customer> userRoot) {
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
        Root<Customer> countRoot = countQuery.from(Customer.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
