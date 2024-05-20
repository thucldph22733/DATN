package com.example.shoesmanagement.service;



import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.GioHangChiTiet;


import java.util.List;
import java.util.UUID;

public interface GHCTService {

    List<GioHangChiTiet> findByGHActive(GioHang gioHang);

    void addNewGHCT(GioHangChiTiet gioHangChiTiet);

    GioHangChiTiet findByCTSPActive(ChiTietGiay chiTietGiay);

    GioHangChiTiet findByCTSP(ChiTietGiay chiTietGiay);

    GioHangChiTiet findByCTGActiveAndKhachHangAndTrangThai(ChiTietGiay chiTietGiay, GioHang gioHang);




}
