package com.mkoner.electronics.express.serviceImpl;

import com.mkoner.electronics.express.entity.Admin;
import com.mkoner.electronics.express.constants.ExceptionMessages;
import com.mkoner.electronics.express.exceptions.CustomerNotFoundException;
import com.mkoner.electronics.express.exceptions.EmailExistException;
import com.mkoner.electronics.express.exceptions.NumberExistException;
import com.mkoner.electronics.express.params.GetUserFilters;
import com.mkoner.electronics.express.params.ProductPage;
import com.mkoner.electronics.express.repository.AdminCriteriaRepository;
import com.mkoner.electronics.express.repository.AdminRepository;
import com.mkoner.electronics.express.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
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
public class AdminServiceImpl implements AdminService, UserDetailsService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private AdminCriteriaRepository adminCriteriaRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    @CacheEvict(value= {"admins", "all-admins"}, allEntries = true)
    public Admin createAdmin(Admin admin) throws EmailExistException, NumberExistException, CustomerNotFoundException {
        String password = passwordEncoder.encode(admin.getPassword());
        validateNewUsernameAndEmail(null, admin.getNumber(), admin.getEmail());
        admin.setPassword(password);
        return adminRepository.save(admin);
    }

    @Override
    @Cacheable(value = "all-admins")
    public List<Admin> getAllAdmins() {
        LOGGER.info("From DB");
        return adminRepository.findAll();
    }

    @Override
    @Cacheable(value = "admins")
    public Page<Admin> getAdminsFilter(ProductPage productPage, GetUserFilters getUserFilters) {
        if(Objects.isNull(productPage.getSortBy()) || productPage.getSortBy().isBlank()){
            productPage.setSortBy("adminId");
        }
        LOGGER.info("From DB");
        return adminCriteriaRepository.findAllFiltered(productPage, getUserFilters);
    }

    @Override
    @CacheEvict(value= {"admins", "all-admins"}, allEntries = true)
    public Admin updateAdmin(Long adminId, Admin admin) throws CustomerNotFoundException, EmailExistException, NumberExistException {
        Optional<Admin> admin1 = adminRepository.findById(adminId);
        if(admin1.isEmpty())
            throw new CustomerNotFoundException(ExceptionMessages.ADMIN_NOT_FOUND);
        Admin adminToUpdate = admin1.get();
        validateNewUsernameAndEmail(adminToUpdate.getNumber(), admin.getNumber(), admin.getEmail());
        if(Objects.nonNull(admin.getEmail()) && !admin.getEmail().isBlank())
            adminToUpdate.setEmail(admin.getEmail());
        if(Objects.nonNull(admin.getName()) && !admin.getName().isBlank())
            adminToUpdate.setName(admin.getName());
        if(Objects.nonNull(admin.getNumber()) && !admin.getNumber().isBlank())
            adminToUpdate.setNumber(admin.getNumber());
        if(Objects.nonNull(admin.getPassword()) && !admin.getPassword().isBlank())
            adminToUpdate.setPassword(passwordEncoder.encode(admin.getPassword()));
        if(Objects.nonNull(admin.getIsEnabled()))
            adminToUpdate.setIsEnabled(admin.getIsEnabled());
        adminToUpdate.setModifiedAt(new Date());
        return adminRepository.save(adminToUpdate);
    }

    @Override
    public Admin getAdminById(Long adminId) throws CustomerNotFoundException {
        Optional<Admin> customer = adminRepository.findById(adminId);
        if(customer.isEmpty())
            throw new CustomerNotFoundException(ExceptionMessages.ADMIN_NOT_FOUND);
        return customer.get();
    }

    @Override
    @CacheEvict(value= {"admins", "all-admins"}, allEntries = true)
    public Void deleteAdmin(Long adminId) throws CustomerNotFoundException {
        getAdminById(adminId);
        adminRepository.deleteById(adminId);
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return adminRepository.findByNumberIgnoreCase(username);
    }

    private Admin validateNewUsernameAndEmail(String currentNumber, String newNumber, String newEmail) throws
            CustomerNotFoundException, NumberExistException, EmailExistException{
        Admin adminByNewNumber = adminRepository.findByNumberIgnoreCase(newNumber);
        Admin adminByNewEmail = adminRepository.findByEmailIgnoreCase(newEmail);
        if(currentNumber != null) {
            Admin currentUser = adminRepository.findByNumberIgnoreCase(currentNumber);
            if(currentUser == null) {
                throw new CustomerNotFoundException(ExceptionMessages.ADMIN_NOT_FOUND);
            }
            if(adminByNewNumber != null && !currentUser.getAdminId().equals(adminByNewNumber.getAdminId())) {
                throw new NumberExistException(ExceptionMessages.NUMBER_EXIST);
            }
            if(adminByNewEmail != null && !currentUser.getAdminId().equals(adminByNewEmail.getAdminId())) {
                throw new EmailExistException(ExceptionMessages.EMAIL_EXIST);
            }
            return currentUser;
        } else {
            if(adminByNewNumber != null) {
                throw new NumberExistException(ExceptionMessages.NUMBER_EXIST);
            }
            if(adminByNewEmail != null) {
                throw new EmailExistException(ExceptionMessages.EMAIL_EXIST);
            }
            return null;
        }
    }
}
