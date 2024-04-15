package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.model.ThongBaoKhachHang;

import java.util.List;

public interface ThongBaoServices {
    void addThongBao(ThongBaoKhachHang thongBaoKhachHang);

    List<ThongBaoKhachHang> getAll();

    List<ThongBaoKhachHang> findByKhachHang(KhachHang khachHang);
}
