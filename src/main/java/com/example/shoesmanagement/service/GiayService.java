package com.example.shoesmanagement.service;



import com.example.shoesmanagement.model.ChatLieu;
import com.example.shoesmanagement.model.Giay;
import com.example.shoesmanagement.model.Hang;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface GiayService {
    public List<Giay> getAllGiay();

    public void save(Giay giay);

    public void deleteByIdGiay(UUID id);

    public Giay getByIdGiay(UUID id);

    public Giay getByName(String name);

    public List<Giay> fillterGiay(String searchTerm);

    public void importDataFromExcel(InputStream excelFile);

    public List<Giay> findByHang(Hang hang);

    public List<Giay> findByChatLieu(ChatLieu chatLieu);

    public List<Giay> findByTrangThai(int trangThai);
}
