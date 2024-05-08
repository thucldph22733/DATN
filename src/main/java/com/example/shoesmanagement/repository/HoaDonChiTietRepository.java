//package com.example.shoesmanagement.repository;
//
//import com.example.shoesmanagement.model.HoaDon;
//import com.example.shoesmanagement.model.HoaDonChiTiet;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Controller;
//
//import java.util.List;
//import java.util.UUID;
//@Controller
//public interface HoaDonChiTietRepository  extends JpaRepository<HoaDonChiTiet, UUID> {
//    @Query(value = "select hdct from HoaDonChiTiet hdct where hdct.hoaDon.idHD = ?1 and hdct.chiTietGiay.idCTG =?2")
//    HoaDonChiTiet findByIdHoaDonAndIdChiTietGiay(UUID idHoaDon, UUID idChiTietGiay);
//
//    @Query(value = "select * from hoa_don_chi_tiet where id_hd = ?1 and trang_thai = 1", nativeQuery = true)
//    List<HoaDonChiTiet> findByIdHoaDon(UUID idHoaDon);
//
//    List<HoaDonChiTiet> findByHoaDon(HoaDon hoaDon);
//}
