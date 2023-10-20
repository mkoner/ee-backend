package com.mkoner.electronics.express.controllers;

import com.mkoner.electronics.express.Utility.JwtTokenProvider;
import com.mkoner.electronics.express.constants.AuthConstants;
import com.mkoner.electronics.express.entity.Customer;
import com.mkoner.electronics.express.exceptions.AuthFailure;
import com.mkoner.electronics.express.exceptions.CustomerNotFoundException;
import com.mkoner.electronics.express.exceptions.EmailExistException;
import com.mkoner.electronics.express.exceptions.NumberExistException;
import com.mkoner.electronics.express.params.GetUserFilters;
import com.mkoner.electronics.express.params.LoginParams;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.service.AdminService;
import com.mkoner.electronics.express.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CustomerService customerService;


    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    @PostMapping("")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) throws EmailExistException, NumberExistException, CustomerNotFoundException {
        LOGGER.info(customer.toString());
        return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginParams loginParams) throws AuthFailure {
        authenticate(loginParams.getNumber(), loginParams.getPassword());
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginParams.getNumber(), loginParams.getPassword()));
        UserDetails loggedInUserDetails = customerService.loadUserByUsername(loginParams.getNumber());
        HttpHeaders jwtHeader = getJwtHeader(loggedInUserDetails);
        return new ResponseEntity<>("Success", jwtHeader, HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<Customer> getAllCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("")
    public Page<Customer> getCustomersFilter(ProductPage productPage, GetUserFilters getUserFilters){
        return customerService.getCustomersFilter(productPage, getUserFilters);
    }

    @GetMapping("/id/{id}")
    public Customer getCustomerById(@PathVariable("id") Long id) throws CustomerNotFoundException {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/number/{number}")
    public Customer getCustomerByNumber(@PathVariable("number") String number) throws CustomerNotFoundException {
        return customerService.getCustomerByNumber(number);
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable("id") Long id, @RequestBody Customer customer) throws EmailExistException, NumberExistException, CustomerNotFoundException {
        return customerService.updateCustomer(id, customer);
    }

    @PutMapping("/open/{id}")
    public Customer updateCustomerOpen(@PathVariable("id") Long id, @RequestBody Customer customer) throws EmailExistException, NumberExistException, CustomerNotFoundException {
        return customerService.updateCustomer(id, customer);
    }


    @DeleteMapping("/{id}")
    public Void deleteCustomer(@PathVariable("id") Long id) throws CustomerNotFoundException {
        return customerService.deleteCustomer(id);
    }

    private HttpHeaders getJwtHeader(UserDetails loggedInClient) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AuthConstants.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(loggedInClient));
        return headers;
    }

    private void authenticate(String username, String password) throws AuthFailure {
        UserDetails customer = customerService.loadUserByUsername(username);
        if (customer==null)
            throw new AuthFailure(AuthConstants.AUTH_FAILURE);
        if(BCrypt.checkpw(password, customer.getPassword()))
            return;
        throw new AuthFailure(AuthConstants.AUTH_FAILURE);

    }
}
