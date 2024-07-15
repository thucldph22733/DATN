package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.ChatLieu;
import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.model.NhanVien;

import java.util.List;
import java.util.UUID;

public interface HoaDonService {
    List<HoaDon> getListHoaDonChuaThanhToan();

    List<HoaDon> getListHoaDonDaThanhToan();

    void add(HoaDon hoaDon);

    public void save(HoaDon hoaDon);

    HoaDon getOne(UUID id);

    List<HoaDon> listHoaDonKhachHangAndTrangThaiOnline(KhachHang khachHang, int trangThai);

    List<HoaDon> findHoaDonByKhachHang(KhachHang khachHang);

    List<HoaDon> listHoaDonOnline();

    List<HoaDon> listHoaDonByNhanVienAndTrangThai(NhanVien nhanVien, int trangThai);

    public void deleteHoaDonCho(UUID id);

    List<HoaDon> listHoaDonHuyAndThanhCongByNhanVien(NhanVien nhanVien);

    List<HoaDon> listAllHoaDonByNhanVien(NhanVien nhanVien);

    double getTongTienSanPham(UUID idHoaDon);

    List<HoaDon> listAllHoaDonByNhanVienHienTai(NhanVien nhanVien);

    List<HoaDon> getAllHoaDonOffLine();
    void updateHoaDon(HoaDon hoaDon);
}
