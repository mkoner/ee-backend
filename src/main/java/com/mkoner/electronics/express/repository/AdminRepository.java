package com.mkoner.electronics.express.repository;

import com.mkoner.electronics.express.entity.Admin;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByNumberIgnoreCase(String number);
    Admin findByEmailIgnoreCase(String email);
}
