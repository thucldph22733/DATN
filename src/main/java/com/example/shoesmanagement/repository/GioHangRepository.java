package com.example.shoesmanagement.repository;

import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang,UUID> {
    GioHang findByKhachHang(KhachHang kh);

}
