//package com.example.shoesmanagement.repository;
//
//import com.example.shoesmanagement.model.HoaDon;
//import com.example.shoesmanagement.model.KhuyenMai;
//import com.example.shoesmanagement.service.KhuyenMaiChiTietHoaDonService;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.UUID;
//
//@Repository
//public interface KhuyenMaiChiTietHoaDonRepository extends JpaRepository<KhuyenMaiChiTietHoaDonService, UUID> {
//    KhuyenMaiChiTietHoaDonService findByKhuyenMaiAndAndHoaDon(KhuyenMai khuyenMai, HoaDon hoaDon);
//
//    KhuyenMaiChiTietHoaDonService findKhuyenMaiChiTietHoaDonByHoaDon(HoaDon hoaDon);
//
//}
