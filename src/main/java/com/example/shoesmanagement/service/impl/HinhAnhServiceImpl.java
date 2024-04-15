package com.example.shoesmanagement.service.impl;



import com.example.shoesmanagement.model.HinhAnh;
import com.example.shoesmanagement.repository.HinhAnhRepository;
import com.example.shoesmanagement.service.HinhAnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HinhAnhServiceImpl implements HinhAnhService {
    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Override
    public List<HinhAnh> getAllHinhAnh() {
        return hinhAnhRepository.findAllByOrderByTgThemDesc();
    }

    @Override
    public void save(HinhAnh hinhAnh) {
        hinhAnhRepository.save(hinhAnh);
    }

    @Override
    public void deleteByIdHinhAnh(UUID id) {
        hinhAnhRepository.deleteById(id);
    }

    @Override
    public HinhAnh getByIdHinhAnh(UUID id) {
        return hinhAnhRepository.findById(id).orElse(null);
    }

    @Override
    public List<HinhAnh> filterHinhAnh(String ma) {
        if ("Mã".equals(ma)) {
            return hinhAnhRepository.findAll();
        }
        return hinhAnhRepository.findMa(ma);
    }
}
