//package com.example.shoesmanagement.service.impl;
//
//import com.example.shoesmanagement.model.HoaDon;
//import com.example.shoesmanagement.repository.KhuyenMaiChiTietHoaDonRepository;
//import com.example.shoesmanagement.service.KhuyenMaiChiTietHoaDonService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class KhuyenMaiChiTietHoaDonImpl implements KhuyenMaiChiTietHoaDonService {
//
//    @Autowired
//    private KhuyenMaiChiTietHoaDonRepository khuyenMaiChiTietHoaDonRepository;
//
//    @Override
//    public KhuyenMaiChiTietHoaDonService addKMCTHD(KhuyenMaiChiTietHoaDonService khuyenMaiChiTietHoaDon) {
//        return khuyenMaiChiTietHoaDonRepository.save(khuyenMaiChiTietHoaDon);
//    }
//
//    @Override
//    public KhuyenMaiChiTietHoaDonService findByHoaDon(HoaDon hoaDon) {
//        return khuyenMaiChiTietHoaDonRepository.findKhuyenMaiChiTietHoaDonByHoaDon(hoaDon);
//    }
//}
