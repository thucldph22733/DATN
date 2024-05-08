package com.example.shoesmanagement.service.impl;



import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.GioHangChiTiet;
import com.example.shoesmanagement.repository.GHCTRepository;

import com.example.shoesmanagement.service.GHCTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GHCTServiceImpl implements GHCTService {


    @Autowired
    private GHCTRepository ghctRepository;

    @Override
    public List<GioHangChiTiet> findByGHActive(GioHang gioHang) {
        return ghctRepository.findByTrangThaiAndGioHangOrderByTgThemDesc(1, gioHang);
    }

    @Override
    public void addNewGHCT(GioHangChiTiet gioHangChiTiet) {
        ghctRepository.save(gioHangChiTiet);
    }

    @Override
    public GioHangChiTiet findByCTSPActive(ChiTietGiay chiTietGiay) {
        return ghctRepository.findByChiTietGiayAndTrangThai(chiTietGiay, 1);
    }

    @Override
    public GioHangChiTiet findByCTSP(ChiTietGiay chiTietGiay) {
        return ghctRepository.findByChiTietGiay(chiTietGiay);
    }

    @Override
    public GioHangChiTiet findByCTGActiveAndKhachHangAndTrangThai(ChiTietGiay chiTietGiay,GioHang gioHang) {
        return ghctRepository.findByChiTietGiayAndTrangThaiAndGioHang(chiTietGiay,1, gioHang);
    }
}
