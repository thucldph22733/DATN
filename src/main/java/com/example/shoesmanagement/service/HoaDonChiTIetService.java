package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.HoaDonChiTiet;

import java.util.List;
import java.util.UUID;

public interface HoaDonChiTIetService {
    HoaDonChiTiet getOne(UUID idHoaDon, UUID idChiTietGiay);

    void add(HoaDonChiTiet hoaDonChiTiet);

    List<HoaDonChiTiet> findByIdHoaDon(UUID id);

    Double tongTien(List<HoaDonChiTiet> list);

    Double tongTienSanPham(List<HoaDonChiTiet> list);

    List<HoaDonChiTiet> findByHoaDon(HoaDon hoaDon);
}
