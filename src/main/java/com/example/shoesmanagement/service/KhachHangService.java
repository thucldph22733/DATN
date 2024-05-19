package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.KhachHang;

import java.util.List;
import java.util.UUID;

public interface KhachHangService {
    KhachHang checkLoginEmail(String email, String pass);
    KhachHang checkEmail(String email);

    public List<KhachHang> getAllKhachHang();

    public void save(KhachHang khachHang);
    public void getByIdKhachHang(UUID id);

    public void addKhachHang(KhachHang khachHang);
    public List<KhachHang> findByTrangThai(int trangThai);
    public List<KhachHang> fillterKhachHang(String maKh, String tenKh);

    List<KhachHang> findKhachHangByTrangThai();
    List<KhachHang> findKhachHangByKeyword(String keyword);
}
