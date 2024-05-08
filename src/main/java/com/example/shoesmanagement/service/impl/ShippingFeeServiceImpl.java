package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.HoaDonChiTiet;
import com.example.shoesmanagement.model.Province;
import com.example.shoesmanagement.repository.HoaDonChiTietRepository;
import com.example.shoesmanagement.service.HoaDonChiTIetService;
import com.example.shoesmanagement.service.ShippingFeeService;
import com.example.shoesmanagement.service.ThanhPhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShippingFeeServiceImpl implements ShippingFeeService {
    @Autowired
    private HoaDonChiTIetService hoaDonChiTietService;

    @Autowired
    private ThanhPhoService thanhPhoService;
    @Override
    public Double calculatorShippingFee(HoaDon hoaDon, Double giaTriMacDinh) {

        Double shippingFee = 0.0;
        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietService.findByHoaDon(hoaDon);

        String diaChiChiTiet = hoaDon.getGiaoHang().getDiaChiNguoiNhan();

        String[] parts = diaChiChiTiet.split(",");
        String thanhPho = parts[parts.length - 1].trim();

        int tongSP = 0;
        int trongLuong = 0;

        Province province = thanhPhoService.findByNameProvince(thanhPho);

        if(province == null){
            List<Province> provinceList =  thanhPhoService.getAll();
            for (Province xxxx: provinceList) {
                if(thanhPho.contains(xxxx.getNameProvince())){
                    province = xxxx;
                }
            }
        }

        List<ChiTietGiay> chiTietGiayList = new ArrayList<>();

        for (HoaDonChiTiet hoaDonChiTiet: hoaDonChiTietList) {
            chiTietGiayList.add(hoaDonChiTiet.getChiTietGiay());
            ChiTietGiay chiTietGiay = hoaDonChiTiet.getChiTietGiay();
            trongLuong = trongLuong + chiTietGiay.getTrongLuong()*hoaDonChiTiet.getSoLuong();
            tongSP = tongSP + hoaDonChiTiet.getSoLuong();
        }

        shippingFee = giaTriMacDinh*province.getTransportCoefficient();

        return shippingFee;
    }

    @Override
    public Integer tinhNgayNhanDuKien(HoaDon hoaDon) {


        String diaChiChiTiet = hoaDon.getGiaoHang().getDiaChiNguoiNhan();

        String[] parts = diaChiChiTiet.split(",");
        String thanhPho = parts[parts.length - 1].trim();

        int soNgaySauDo = 0;
        Province province = thanhPhoService.findByNameProvince(thanhPho);
        if(province == null){
            List<Province> provinceList =  thanhPhoService.getAll();
            for (Province xxxx: provinceList) {
                if(thanhPho.contains(xxxx.getNameProvince())){
                    province = xxxx;
                }
            }
        }
        soNgaySauDo = province.getTransportCoefficient();
        return soNgaySauDo;
    }
}
