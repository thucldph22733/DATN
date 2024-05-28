package com.example.shoesmanagement.repository;

import com.example.shoesmanagement.model.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, UUID> {
    List<KhuyenMai> findAll();
}
