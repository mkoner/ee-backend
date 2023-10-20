package com.mkoner.electronics.express.repository;

import com.mkoner.electronics.express.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryNameIgnoreCase(String categoryName);
}
