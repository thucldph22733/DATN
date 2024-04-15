package com.example.shoesmanagement.service;



import com.example.shoesmanagement.model.HinhAnh;

import java.util.List;
import java.util.UUID;

public interface HinhAnhService {
    public List<HinhAnh> getAllHinhAnh();

    public void save(HinhAnh hinhAnh);

    public void deleteByIdHinhAnh(UUID id);

    public HinhAnh getByIdHinhAnh(UUID id);

    public List<HinhAnh> filterHinhAnh(String ma);
}
