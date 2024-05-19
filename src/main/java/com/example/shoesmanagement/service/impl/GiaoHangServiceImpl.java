package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.GiaoHang;
import com.example.shoesmanagement.repository.GiaoHangRepository;
import com.example.shoesmanagement.repository.GioHangRepository;
import com.example.shoesmanagement.service.GiaoHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class GiaoHangServiceImpl implements GiaoHangService {
    @Autowired
    private GiaoHangRepository giaoHangRepository;
    @Override
    public void saveGiaoHang(GiaoHang giaoHang) {
        giaoHangRepository.save(giaoHang);

    }
}
