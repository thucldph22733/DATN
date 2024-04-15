package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.model.ThongBaoKhachHang;
import com.example.shoesmanagement.repository.ThongBaoRepository;
import com.example.shoesmanagement.service.ThongBaoServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThongBaoKhachHangServiceImpls implements ThongBaoServices {
    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Override
    public void addThongBao(ThongBaoKhachHang thongBaoKhachHang) {
        thongBaoRepository.save(thongBaoKhachHang);
    }

    @Override
    public List<ThongBaoKhachHang> getAll() {
        return thongBaoRepository.findAllByOrderByTgTBDesc();
    }

    @Override
    public List<ThongBaoKhachHang> findByKhachHang(KhachHang khachHang) {
        return thongBaoRepository.findByKhachHangOrderByTgTBDesc(khachHang);
    }
}
