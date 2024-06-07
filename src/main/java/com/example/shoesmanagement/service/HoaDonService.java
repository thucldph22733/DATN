package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.model.NhanVien;

import java.util.List;
import java.util.UUID;

public interface HoaDonService {
    List<HoaDon> getListHoaDonChuaThanhToan();
    void add(HoaDon hoaDon);
    HoaDon getOne(UUID id);
    List<HoaDon> listHoaDonKhachHangAndTrangThaiOnline(KhachHang khachHang, int trangThai);
    List<HoaDon> findHoaDonByKhachHang(KhachHang khachHang);
    List<HoaDon>  listHoaDonOnline();
    List<HoaDon> listHoaDonByNhanVienAndTrangThai(NhanVien nhanVien, int trangThai);

    List<HoaDon> listHoaDonHuyAndThanhCongByNhanVien(NhanVien nhanVien);

    List<HoaDon> listAllHoaDonByNhanVien(NhanVien nhanVien);

    List<HoaDon> listAllHoaDonByNhanVienHienTai(NhanVien nhanVien);

//    List<HoaDon> hienThiDieuKien();
}
