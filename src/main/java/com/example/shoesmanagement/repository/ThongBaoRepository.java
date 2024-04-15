package com.example.shoesmanagement.repository;

import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.model.ThongBaoKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBaoKhachHang, UUID> {
    List<ThongBaoKhachHang> findAllByOrderByTgTBDesc();

    List<ThongBaoKhachHang> findByKhachHangOrderByTgTBDesc(KhachHang khachHang);
}
