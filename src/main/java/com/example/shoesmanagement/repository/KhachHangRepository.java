package com.example.shoesmanagement.repository;

import com.example.shoesmanagement.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KhachHangRepository extends JpaRepository< KhachHang,UUID> {
    KhachHang findByEmailKHAndTrangThaiAndMatKhau(String email,int trangThai,String pass);
    KhachHang findByEmailKH(String email);
    List<KhachHang> findByTrangThai(int trangThai);
    List<KhachHang> findByMaKHOrHoTenKH(String maKH, String tenKH);
    KhachHang findByHoTenKH(String name);
    KhachHang findBySdtKH(String sdtKH);
    @Query(value = "select * from khach_hang where trang_thai =1",nativeQuery = true)
    List<KhachHang> getKhachHangByTrangThai();
    @Query(value = "select * from khach_hang where trang_thai=1 and ho_ten_kh like %1% or sdt_kh like %1%",nativeQuery = true)
    List<KhachHang> findByHoTenKHOrSdtKH(String keyword);
    boolean existsBySdtKH(String sdtKH);
}
