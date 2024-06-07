package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.HoaDonChiTiet;
import com.example.shoesmanagement.repository.HoaDonChiTietRepository;
import com.example.shoesmanagement.repository.HoaDonRepository;
import com.example.shoesmanagement.service.HoaDonChiTIetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HoaDonChiTietServiceImpl implements HoaDonChiTIetService {
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Override
    public HoaDonChiTiet getOne(UUID idHoaDon, UUID idChiTietGiay) {
        return hoaDonChiTietRepository.findByIdHoaDonAndIdChiTietGiay(idHoaDon,idChiTietGiay);
    }

    @Override
    public void add(HoaDonChiTiet hoaDonChiTiet) {
        hoaDonChiTietRepository.save(hoaDonChiTiet);

    }

    @Override
    public List<HoaDonChiTiet> findByIdHoaDon(UUID id) {
        return hoaDonChiTietRepository.findByIdHoaDon(id);
    }

    @Override
    public Double tongTien(List<HoaDonChiTiet> list) {
        double tongTien = 0;
        for (HoaDonChiTiet hoaDonChiTiet : list){
            if (hoaDonChiTiet.getTrangThai() == 1){
                tongTien += hoaDonChiTiet.getDonGia();
            }
        }
        return tongTien;
    }

    @Override
    public Double tongTienSanPham(List<HoaDonChiTiet> list) {
        double tongTienSanPham = 0;
        for (HoaDonChiTiet hoaDonChiTiet : list){
            if (hoaDonChiTiet.getTrangThai() == 1){
                tongTienSanPham += hoaDonChiTiet.getDonGia();
            }
        }
        return tongTienSanPham;
    }

    @Override
    public List<HoaDonChiTiet> findByHoaDon(HoaDon hoaDon) {
        return hoaDonChiTietRepository.findByHoaDon(hoaDon);
    }
}
