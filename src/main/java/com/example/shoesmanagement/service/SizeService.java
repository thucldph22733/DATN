package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.Size;
import jakarta.transaction.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface SizeService {
    public List<Size> getAllSizeActiveOrderByName();

    public List<Size> getAllSize();

    public void save(Size size);

    public void deleteByIdSize(UUID id);

    public Size getByIdSize(UUID id);

    public List<Size> getByActive();

    public List<Size> filterSizes(Integer selectedSize, String maSize);
}
