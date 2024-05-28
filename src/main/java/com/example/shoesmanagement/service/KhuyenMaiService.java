package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.KhuyenMai;

import java.util.List;
import java.util.UUID;

public interface KhuyenMaiService {
    public List<KhuyenMai> getAllKhuyenMai();

    public void save(KhuyenMai khuyenMai);

    public KhuyenMai getByIdKhuyenMai(UUID id);
}
