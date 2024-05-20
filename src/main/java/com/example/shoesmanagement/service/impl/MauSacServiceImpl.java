package com.example.shoesmanagement.service.impl;


import com.example.shoesmanagement.model.MauSac;
import com.example.shoesmanagement.repository.MauSacRepository;
import com.example.shoesmanagement.service.MauSacService;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class MauSacServiceImpl implements MauSacService {
    @Autowired
    private MauSacRepository mauSacRepository;

    @Override
    public List<MauSac> getALlMauSac() {
        return mauSacRepository.findAllByOrderByTgThemDesc();
    }

    @Override
    public void save(MauSac mauSac) {
        mauSacRepository.save(mauSac);
    }

    @Override
    public MauSac getByIdMauSac(UUID id) {
        return mauSacRepository.findById(id).orElse(null);
    }

    @Override
    public List<MauSac> getMauSacActive() {
        return mauSacRepository.findByTrangThai(1);
    }

    @Override
    public List<MauSac> filterMauSac(String maMau, String tenMau) {
        if ("Mã Màu".equals(maMau) && "Tên Màu".equals(tenMau)) {
            return mauSacRepository.findAll();
        }
        return mauSacRepository.findByMaMauOrTenMau(maMau, tenMau);
    }


}
