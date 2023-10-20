package com.mkoner.electronics.express.serviceImpl;

import com.mkoner.electronics.express.entity.Customer;
import com.mkoner.electronics.express.constants.ExceptionMessages;
import com.mkoner.electronics.express.exceptions.CustomerNotFoundException;
import com.mkoner.electronics.express.exceptions.EmailExistException;
import com.mkoner.electronics.express.exceptions.NumberExistException;
import com.mkoner.electronics.express.params.GetUserFilters;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.repository.CustomerCriteriaRepository;
import com.mkoner.electronics.express.repository.CustomerRepository;
import com.mkoner.electronics.express.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService, UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerCriteriaRepository customerCriteriaRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    @CacheEvict(value = {"all-customers", "customers"}, allEntries = true)
    public Customer createCustomer(Customer customer) throws EmailExistException, NumberExistException, CustomerNotFoundException {
        String password = passwordEncoder.encode(customer.getPassword());
        validateNewUsernameAndEmail(null, customer.getNumber());
        customer.setPassword(password);
        return customerRepository.save(customer);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByNumberIgnoreCase(username);
    }

    @Override
    @Cacheable(value = "all-customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Cacheable(value = "customers")
    public Page<Customer> getCustomersFilter(ProductPage productPage, GetUserFilters getUserFilters) {
        if(Objects.isNull(productPage.getSortBy()) || productPage.getSortBy().isBlank()){
            productPage.setSortBy("customerId");
        }
        return customerCriteriaRepository.findAllFiltered(productPage, getUserFilters);
    }

    @Override
    @CacheEvict(value = {"all-customers", "customers"}, allEntries = true)
    public Customer updateCustomer(Long customerId, Customer customer) throws CustomerNotFoundException, EmailExistException, NumberExistException {
        Optional<Customer> customer1 = customerRepository.findById(customerId);
        if(customer1.isEmpty())
            throw new CustomerNotFoundException(ExceptionMessages.CUSTOMER_NOT_FOUND);
        Customer customerToUpdate = customer1.get();
        validateNewUsernameAndEmail(customerToUpdate.getNumber(), customer.getNumber());
        if(Objects.nonNull(customer.getEmail()) && !customer.getEmail().isBlank())
            customerToUpdate.setEmail(customer.getEmail());
        if(Objects.nonNull(customer.getName()) && !customer.getName().isBlank())
            customerToUpdate.setName(customer.getName());
        if(Objects.nonNull(customer.getNumber()) && !customer.getNumber().isBlank())
            customerToUpdate.setNumber(customer.getNumber());
        if(Objects.nonNull(customer.getPassword()) && !customer.getPassword().isBlank())
            customerToUpdate.setPassword(passwordEncoder.encode(customer.getPassword()));
        if(Objects.nonNull(customer.getIsEnabled()))
            customerToUpdate.setIsEnabled(customer.getIsEnabled());
        customerToUpdate.setModifiedAt(new Date());
        return customerRepository.save(customerToUpdate);
    }

    @Override
    public Customer getCustomerById(Long customerId) throws CustomerNotFoundException {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if(customer.isEmpty())
            throw new CustomerNotFoundException(ExceptionMessages.CUSTOMER_NOT_FOUND);
        return customer.get();
    }

    @Override
    public Customer getCustomerByNumber(String number) throws CustomerNotFoundException {
        Customer customer = customerRepository.findByNumberIgnoreCase(number);
        if(customer==null)
            throw new CustomerNotFoundException(ExceptionMessages.CUSTOMER_NOT_FOUND);
        return customer;
    }

    @Override
    @CacheEvict(value = {"all-customers", "customers"}, allEntries = true)
    public Void deleteCustomer(Long customerId) throws CustomerNotFoundException {
        getCustomerById(customerId);
        customerRepository.deleteById(customerId);
        return null;
    }

    private Customer validateNewUsernameAndEmail(String currentNumber, String newNumber) throws
            CustomerNotFoundException, NumberExistException, EmailExistException{
        Customer customerByNewNumber = customerRepository.findByNumberIgnoreCase(newNumber);
        if(currentNumber != null) {
            Customer currentUser = customerRepository.findByNumberIgnoreCase(currentNumber);
            if(currentUser == null) {
                throw new CustomerNotFoundException(ExceptionMessages.CUSTOMER_NOT_FOUND);
            }
            if(customerByNewNumber != null && !currentUser.getCustomerId().equals(customerByNewNumber.getCustomerId())) {
                throw new NumberExistException(ExceptionMessages.NUMBER_EXIST);
            }

            return currentUser;
        } else {
            if(customerByNewNumber != null) {
                throw new NumberExistException(ExceptionMessages.NUMBER_EXIST);
            }
            return null;
        }
    }

}
