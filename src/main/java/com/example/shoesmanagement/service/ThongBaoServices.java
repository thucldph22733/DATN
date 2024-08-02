package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.KhachHang;

import java.util.List;

public interface ThongBaoServices {
    void addThongBao(ThongBaoKhachHang thongBaoKhachHang);

    List<ThongBaoKhachHang> getAll();

    List<ThongBaoKhachHang> findByKhachHang(KhachHang khachHang);
}
