package com.example.shoesmanagement.repository;

import com.example.shoesmanagement.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ThanhPhoRepository extends JpaRepository<Province, UUID> {
    @Query(value = "select * from  province WHERE province_name like ?1", nativeQuery = true)
    Province findByNameProvince(String nameProvince);
}
