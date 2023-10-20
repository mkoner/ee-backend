package com.mkoner.electronics.express.repository;

import com.mkoner.electronics.express.entity.Order;
import com.mkoner.electronics.express.params.GetOrderFilters;
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
public class OrderCriteriaRepository {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public OrderCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Order> findAllFiltered(ProductPage productPage, GetOrderFilters getOrderFilters){
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> orderRoot = criteriaQuery.from(Order.class);
        Predicate predicate = getPredicate(getOrderFilters, orderRoot);
        criteriaQuery.where(predicate);
        setOrder(productPage, criteriaQuery, orderRoot);

        TypedQuery<Order> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(productPage.getCurrentPage() * productPage.getPageSize());
        typedQuery.setMaxResults(productPage.getPageSize());

        Pageable pageable = getPageable(productPage);

        long usersCount = getProductsCount(predicate);

        return new PageImpl<>(typedQuery.getResultList(), pageable, usersCount);
    }

    private Predicate getPredicate(GetOrderFilters getOrderFilters, Root<Order> orderRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if(Objects.nonNull(getOrderFilters.getOrderId())){
            predicates.add(
                    criteriaBuilder.equal(orderRoot.get("orderId"),
                            getOrderFilters.getOrderId())
            );
        }
        if(Objects.nonNull(getOrderFilters.getStatus()) && !getOrderFilters.getStatus().isBlank()){
            predicates.add(
                    criteriaBuilder.equal(orderRoot.get("orderStatus"),
                           getOrderFilters.getStatus())
            );
        }
        if(Objects.nonNull(getOrderFilters.getMaxPrice()) && Objects.nonNull((getOrderFilters.getMinPrice()))){
            predicates.add(
                    criteriaBuilder.between(orderRoot.get("orderAmount"),
                            getOrderFilters.getMinPrice(), getOrderFilters.getMaxPrice())
            );
        }

        if(Objects.nonNull(getOrderFilters.getMaxPrice()) && !Objects.nonNull((getOrderFilters.getMinPrice()))){
            predicates.add(
                    criteriaBuilder.le(orderRoot.get("orderAmount"),
                            getOrderFilters.getMaxPrice())
            );
        }

        if(!Objects.nonNull(getOrderFilters.getMaxPrice()) && Objects.nonNull((getOrderFilters.getMinPrice()))){
            predicates.add(
                    criteriaBuilder.ge(orderRoot.get("orderAmount"),
                            getOrderFilters.getMinPrice())
            );
        }
        if(Objects.nonNull(getOrderFilters.getCustomerName()) && !getOrderFilters.getCustomerName().isBlank()){
            predicates.add(
                    criteriaBuilder.like(orderRoot.get("customer").get("name"),
                            "%" + getOrderFilters.getCustomerName()+ "%")
            );
        }
        if(Objects.nonNull(getOrderFilters.getCustomerNumber()) && !getOrderFilters.getCustomerNumber().isBlank()){
            predicates.add(
                    criteriaBuilder.like(orderRoot.get("customer").get("number"),
                            "%" + getOrderFilters.getCustomerNumber()+ "%")
            );
        }

        if(Objects.nonNull(getOrderFilters.getCity()) && !getOrderFilters.getCity().isBlank()){
            predicates.add(
                    criteriaBuilder.like(orderRoot.get("shippingAddress").get("addressCity"),
                            "%" + getOrderFilters.getCity()+ "%")
            );
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void setOrder(ProductPage productPage,
                          CriteriaQuery<Order> criteriaQuery,
                          Root<Order> userRoot) {
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
        Root<Order> countRoot = countQuery.from(Order.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
