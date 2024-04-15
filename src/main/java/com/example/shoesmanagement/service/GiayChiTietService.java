package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.*;


import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface GiayChiTietService {

    public List<ChiTietGiay> getAllChiTietGiay();

    public List<ChiTietGiay> getTop4ChiTietGiay();


    public List<ChiTietGiay> getCTGByGIayActive(Giay giay);

    public void save(ChiTietGiay chiTietGiay);

    public void update(ChiTietGiay chiTietGiay);

    public void deleteByIdChiTietGiay(UUID id);

    public ChiTietGiay getByIdChiTietGiay(UUID id);

    public List<ChiTietGiay> findByGiay(Giay giay);

    public List<ChiTietGiay> findByMauSac(MauSac mauSac);

    public List<ChiTietGiay> findBySize(Size size);


    public List<ChiTietGiay> getCTGByGiay(Giay giay);

    List<ChiTietGiay> isDuplicateChiTietGiay(UUID giayId, UUID sizeId, UUID mauSacId, UUID hinhAnhId);


    List<ChiTietGiay> findByGiayAndMau(Giay giay, MauSac mauSac);

    List<ChiTietGiay> findByMauSacAndGiay(MauSac mauSac, Giay giay, int trangThai);


    HinhAnh hinhAnhByGiayAndMau(Giay giay, MauSac mauSac);


}
