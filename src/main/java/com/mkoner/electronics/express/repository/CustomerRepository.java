package com.mkoner.electronics.express.repository;

import com.mkoner.electronics.express.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByEmailIgnoreCase(String email);
    Customer findByNumberIgnoreCase(String number);
}
