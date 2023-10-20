package com.mkoner.electronics.express.repository;

import com.mkoner.electronics.express.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FileRepository extends JpaRepository<File, String> {
}
