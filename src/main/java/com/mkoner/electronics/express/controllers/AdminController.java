package com.mkoner.electronics.express.controllers;

import com.mkoner.electronics.express.Utility.JwtTokenProvider;
import com.mkoner.electronics.express.constants.AuthConstants;
import com.mkoner.electronics.express.entity.Admin;
import com.mkoner.electronics.express.exceptions.AuthFailure;
import com.mkoner.electronics.express.exceptions.CustomerNotFoundException;
import com.mkoner.electronics.express.exceptions.EmailExistException;
import com.mkoner.electronics.express.exceptions.NumberExistException;
import com.mkoner.electronics.express.params.GetUserFilters;
import com.mkoner.electronics.express.params.LoginParams;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.service.AdminService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController{

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AdminService adminService;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());


    @PostMapping("")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) throws EmailExistException, NumberExistException, CustomerNotFoundException {
        return new ResponseEntity<>(adminService.createAdmin(admin), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginParams loginParams) throws AuthFailure {
        authenticate(loginParams.getNumber(), loginParams.getPassword());
        //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginParams.getNumber(), loginParams.getPassword()));
        UserDetails loggedInUserDetails = adminService.loadUserByUsername(loginParams.getNumber());
        HttpHeaders jwtHeader = getJwtHeader(loggedInUserDetails);
        return new ResponseEntity<>("Success", jwtHeader, HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<Admin> getAllAdmins(){
        return adminService.getAllAdmins();
    }

    @GetMapping("")
    public Page<Admin> getAdminsFilter(ProductPage productPage, GetUserFilters getUserFilters){
        return adminService.getAdminsFilter(productPage, getUserFilters);
    }

    @GetMapping("/id/{id}")
    public Admin getAdminById(@PathVariable("id") Long id) throws CustomerNotFoundException {
        LOGGER.info(String.valueOf(id));
        return adminService.getAdminById(id);
    }

    @PutMapping("/{id}")
    public Admin updateAdmin(@PathVariable("id") Long id, @RequestBody Admin admin) throws EmailExistException, NumberExistException, CustomerNotFoundException {
        LOGGER.info("Called admin update controller", admin.toString());
        return adminService.updateAdmin(id, admin);
    }

    @DeleteMapping("/{id}")
    public Void deleteAdmin(@PathVariable("id") Long id) throws CustomerNotFoundException {
        return adminService.deleteAdmin(id);
    }

    private HttpHeaders getJwtHeader(UserDetails loggedInClient) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AuthConstants.JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(loggedInClient));
        return headers;
    }

    private void authenticate(String username, String password) throws AuthFailure {
        UserDetails admin = adminService.loadUserByUsername(username);
        if (admin==null)
            throw new AuthFailure(AuthConstants.AUTH_FAILURE);
        if(BCrypt.checkpw(password, admin.getPassword()))
            return;
        throw new AuthFailure(AuthConstants.AUTH_FAILURE);

    }
}
