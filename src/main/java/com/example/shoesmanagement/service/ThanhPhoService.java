package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.Province;

import java.util.List;

public interface ThanhPhoService {
    Province findByNameProvince(String nameProvince);
    List<Province> getAll();
}
