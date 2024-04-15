package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.repository.GioHangRepository;
import com.example.shoesmanagement.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GioHangServiceImpl implements GioHangService {
    @Autowired
    private GioHangRepository gioHangRepository;
    @Override
    public GioHang findByKhachHang(KhachHang khachHang) {
        return gioHangRepository.findByKhachHang(khachHang);
    }

    @Override
    public void saveGH(GioHang gh) {gioHangRepository.save(gh);
    }
}
