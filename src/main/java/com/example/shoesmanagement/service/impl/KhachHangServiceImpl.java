package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.repository.KhachHangRepository;
import com.example.shoesmanagement.service.GioHangService;
import com.example.shoesmanagement.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class KhachHangServiceImpl  implements KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Override
    public KhachHang checkLoginEmail(String email, String pass) {
        return khachHangRepository.findByEmailKHAndTrangThaiAndMatKhau(email,1,pass);
    }

    @Override
    public KhachHang findByEmail(String email) {
        return khachHangRepository.findByEmailKH(email);
    }

    @Override
    public boolean changePassword(KhachHang khachHang, String currentPass, String newPass) {
        if (khachHang.getMatKhau().equals(currentPass)) {
            khachHang.setMatKhau(newPass);
            khachHangRepository.save(khachHang);
            return true;
        }
        return false;
    }

    @Override
    public KhachHang checkEmail(String email) {
        return khachHangRepository.findByEmailKH(email);
    }

    @Override
    public KhachHang checkLoginSDT(String sdt, String pass) {
        return khachHangRepository.findBySdtKHAndTrangThaiAndMatKhau(sdt, 1, pass);
    }

    @Override
    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    @Override
    public void save(KhachHang khachHang) {
        khachHangRepository.save(khachHang);
    }

    @Override
    public KhachHang getByIdKhachHang(UUID id) {
        return khachHangRepository.findById(id).orElse(null);
    }

    @Override
    public void addKhachHang(KhachHang khachHang) {
        khachHangRepository.save(khachHang);
    }

    @Override
    public List<KhachHang> findByTrangThai(int trangThai) {
        return khachHangRepository.findByTrangThai(trangThai);
    }

    @Override
    public List<KhachHang> fillterKhachHang(String maKh, String tenKh) {
        if("Mã Khách Hàng".equals(maKh) && "Tên Khách Hàng".equals(tenKh)){
            return khachHangRepository.findAll();
        }
        return khachHangRepository.findByMaKHOrHoTenKH(maKh, tenKh);
    }

    @Override
    public List<KhachHang> findKhachHangByTrangThai() {
        return khachHangRepository.getKhachHangByTrangThai();
    }

    @Override
    public List<KhachHang> findKhachHangByKeyword(String keyword) {
        return khachHangRepository.findByHoTenKHOrSdtKH(keyword);
    }
}
