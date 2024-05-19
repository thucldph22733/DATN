package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.GiaoHang;
import com.example.shoesmanagement.model.ViTriDonHang;

import java.util.List;

public interface ViTriDonHangService {
    List<ViTriDonHang> getAll();
    List<ViTriDonHang> findByGiaoHang(GiaoHang giaoHang);
    void addViTriDonHang(ViTriDonHang viTriDonHang);
}
