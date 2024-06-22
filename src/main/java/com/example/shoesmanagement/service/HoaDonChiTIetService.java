package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.HoaDonChiTiet;

import java.util.List;
import java.util.UUID;

public interface HoaDonChiTIetService {
    HoaDonChiTiet getOne(UUID idHoaDon, UUID idChiTietGiay);

    public void delete(HoaDonChiTiet hoaDonChiTiet);

    void add(HoaDonChiTiet hoaDonChiTiet);

    void save(HoaDonChiTiet hoaDonChiTiet);

    List<HoaDonChiTiet> findByIdHoaDon(UUID id);

    Double tongTien(List<HoaDonChiTiet> list);

    Double tongTienSanPham(List<HoaDonChiTiet> list);

    List<HoaDonChiTiet> findByHoaDon(HoaDon hoaDon);

    void updateQuantity(UUID idHDCT, int newQuantity);

    void updateQuantityGiay(UUID idHDCT, int newQuantity);

    HoaDonChiTiet findByCTGActiveAndKhachHagAndTrangThai(ChiTietGiay chiTietGiay , HoaDon hoaDon);
}
