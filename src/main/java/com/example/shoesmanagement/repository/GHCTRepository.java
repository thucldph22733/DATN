package com.example.shoesmanagement.repository;


import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.GioHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GHCTRepository extends JpaRepository<GioHangChiTiet, UUID> {

    GioHangChiTiet findByChiTietGiayAndTrangThai(ChiTietGiay chiTietGiay, int trangThai);

    GioHangChiTiet findByChiTietGiay(ChiTietGiay chiTietGiay);

    List<GioHangChiTiet> findByTrangThaiAndGioHangOrderByTgThemDesc(int trangThai, GioHang gh);

    GioHangChiTiet findByChiTietGiayAndTrangThaiAndGioHang(ChiTietGiay chiTietGiay, int trangThai, GioHang gioHang);
}
