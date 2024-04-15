package com.example.shoesmanagement.service;


import com.example.shoesmanagement.model.MauSac;

import java.io.InputStream;

import java.util.List;
import java.util.UUID;

public interface MauSacService {

    public List<MauSac> getALlMauSac();

    public void save(MauSac mauSac);

    public MauSac getByIdMauSac(UUID id);

    List<MauSac> getMauSacActive();

    public List<MauSac> filterMauSac(String maMau, String tenMau);


}
