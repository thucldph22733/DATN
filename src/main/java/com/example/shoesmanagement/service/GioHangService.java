package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.KhachHang;

public interface GioHangService {
    GioHang findByKhachHang(KhachHang khachHang);
    void saveGH(GioHang gh);
}
