package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.GiaoHang;
import com.example.shoesmanagement.model.ViTriDonHang;
import com.example.shoesmanagement.repository.ViTriDonHangRepository;
import com.example.shoesmanagement.service.ViTriDonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViTriDonHangServiceImpl  implements ViTriDonHangService {
    @Autowired
    private ViTriDonHangRepository viTriDonHangRepository;
    @Override
    public List<ViTriDonHang> getAll() {
        return viTriDonHangRepository.findAll();
    }

    @Override
    public List<ViTriDonHang> findByGiaoHang(GiaoHang giaoHang) {
        return viTriDonHangRepository.findByGiaoHangOrderByThoiGianDesc(giaoHang);
    }

    @Override
    public void addViTriDonHang(ViTriDonHang viTriDonHang) {
        viTriDonHangRepository.save(viTriDonHang);
    }
}
