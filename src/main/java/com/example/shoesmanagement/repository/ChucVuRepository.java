package com.example.shoesmanagement.repository;

import com.example.shoesmanagement.model.ChucVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, UUID> {
    List<ChucVu> getByTrangThai(int trangThai);

    List<ChucVu> findByMaCVOrTenCV(String maCV, String tenCV);

    ChucVu findByMaCV(String maCV);

    ChucVu findByTenCV(String name);

    @Query(value = "select cv from ChucVu cv order by cv.tgThem desc")
    List<ChucVu> getAllChucVu();
}
