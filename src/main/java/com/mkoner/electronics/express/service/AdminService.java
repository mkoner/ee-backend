package com.mkoner.electronics.express.service;

import com.mkoner.electronics.express.entity.Admin;
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

public interface AdminService {
    Admin createAdmin(Admin admin) throws EmailExistException, NumberExistException, CustomerNotFoundException;
    List<Admin> getAllAdmins();
    Page<Admin> getAdminsFilter(ProductPage productPage, GetUserFilters getUserFilters);
    Admin updateAdmin(Long adminId, Admin admin) throws CustomerNotFoundException, EmailExistException, NumberExistException;
    Admin getAdminById(Long adminId) throws CustomerNotFoundException;
    Void deleteAdmin(Long adminId) throws CustomerNotFoundException;
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
