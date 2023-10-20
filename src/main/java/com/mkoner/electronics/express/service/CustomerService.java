package com.mkoner.electronics.express.service;

import com.mkoner.electronics.express.entity.Customer;
import com.mkoner.electronics.express.exceptions.CustomerNotFoundException;
import com.mkoner.electronics.express.exceptions.EmailExistException;
import com.mkoner.electronics.express.exceptions.NumberExistException;
import com.mkoner.electronics.express.params.GetUserFilters;
import com.mkoner.electronics.express.params.ProductPage;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer) throws EmailExistException, NumberExistException, CustomerNotFoundException;
    List<Customer> getAllCustomers();

    Page<Customer> getCustomersFilter(ProductPage productPage, GetUserFilters getUserFilters);
    Customer updateCustomer(Long customerId, Customer customer) throws CustomerNotFoundException, EmailExistException, NumberExistException;
    Customer getCustomerById(Long customerId) throws CustomerNotFoundException;
    Customer getCustomerByNumber(String number) throws CustomerNotFoundException;
    Void deleteCustomer(Long customerId) throws CustomerNotFoundException;
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
