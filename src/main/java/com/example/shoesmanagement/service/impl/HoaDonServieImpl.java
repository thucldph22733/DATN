package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.model.NhanVien;
import com.example.shoesmanagement.repository.HoaDonRepository;
import com.example.shoesmanagement.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class HoaDonServieImpl implements HoaDonService {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Override
    public List<HoaDon> getListHoaDonChuaThanhToan() {
        return hoaDonRepository.listChuaThanhToan();
    }

    @Override
    public void add(HoaDon hoaDon) {
        hoaDonRepository.save(hoaDon);

    }

    @Override
    public HoaDon getOne(UUID id) {
        return hoaDonRepository.findById(id).orElse(null);
    }

    @Override
    public List<HoaDon> listHoaDonKhachHangAndTrangThaiOnline(KhachHang khachHang, int trangThai) {
        return  hoaDonRepository.findByKhachHangAndLoaiHDAndTrangThaiOrderByTgTaoDesc(khachHang, 0,trangThai);
    }

    @Override
    public List<HoaDon> findHoaDonByKhachHang(KhachHang khachHang) {
        return hoaDonRepository.findByKhachHangAndLoaiHDOrderByTgTaoDesc(khachHang,0);
    }

    @Override
    public List<HoaDon> listHoaDonOnline() {
        return hoaDonRepository.findByLoaiHDOrderByTgTaoDesc(0);
    }

    @Override
    public List<HoaDon> listHoaDonByNhanVienAndTrangThai(NhanVien nhanVien, int trangThai) {
        return hoaDonRepository.findByNhanVienAndLoaiHDAndTrangThaiOrderByTgTaoDesc(nhanVien,0,trangThai);
    }

    @Override
    public List<HoaDon> listHoaDonHuyAndThanhCongByNhanVien(NhanVien nhanVien) {
        return hoaDonRepository.findByNhanVienAndLoaiHDAndTrangThaiOrTrangThaiOrderByTgTaoDesc(nhanVien,0,3,4);
    }

    @Override
    public List<HoaDon> listAllHoaDonByNhanVien(NhanVien nhanVien) {
        return hoaDonRepository.findByNhanVienOrderByTgTaoDesc(nhanVien);
    }

    @Override
    public List<HoaDon> listAllHoaDonByNhanVienHienTai(NhanVien nhanVien) {
        return hoaDonRepository.listAllHoaDonByNhanVienHienTai();
    }
}
