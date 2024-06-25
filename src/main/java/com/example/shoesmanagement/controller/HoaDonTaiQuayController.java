package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.HoaDonChiTiet;
//import com.example.shoesmanagement.model.KhuyenMaiChiTietHoaDon;
import com.example.shoesmanagement.service.HoaDonChiTIetService;
import com.example.shoesmanagement.service.HoaDonService;
//import com.example.shoesmanagement.service.KhuyenMaiChiTietHoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequestMapping("/manage/bill")
@Controller
public class HoaDonTaiQuayController {
    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private HoaDonChiTIetService hoaDonChiTietService;

//    @Autowired
//    private KhuyenMaiChiTietHoaDonService khuyenMaiChiTietHoaDonService;


    @GetMapping("/offline")
    public String getAll(Model model){
        List<HoaDon> hoaDonList = hoaDonService.getAllHoaDonOffLine();

        List<HoaDon> listHoaDonThanhCong = new ArrayList<>();
        List<HoaDon> listHoaDonCho = new ArrayList<>();
        List<HoaDon> listHoaDonHuy = new ArrayList<>();

        int tongHoaDon = hoaDonList.size();
        Double soTienDaThanhToan = 0.0;
        int soLuongHoaDonCho = 0;
        int soLuongHoaDonThanhCong = 0;
        int soLuongHoaDonHuy = 0;
        int soLuongSanPhamDaBan = 0;

        for (HoaDon x: hoaDonList) {
            if (x.getTrangThai() == 0){
                soLuongHoaDonCho ++;
                listHoaDonCho.add(x);
            }
            if (x.getTrangThai() == 1){
                soLuongHoaDonThanhCong ++;
                listHoaDonThanhCong.add(x);
                soLuongSanPhamDaBan += x.getTongSP();
            }
            if(x.getTrangThai() == 2){
                soLuongHoaDonHuy ++;
                listHoaDonHuy.add(x);
            }
        }

        model.addAttribute("soLuongHoaDonTaiQuay",tongHoaDon);
        model.addAttribute("tongTienBanTaiQuay",soTienDaThanhToan);
        model.addAttribute("tongSanPhamDaBan",soLuongSanPhamDaBan);
        model.addAttribute("hoaDonThanhCong",soLuongHoaDonThanhCong);
        model.addAttribute("hoaDonChoTaiQuay",soLuongHoaDonCho);
        model.addAttribute("hoaDonHuy",soLuongHoaDonHuy);

        model.addAttribute("listHoaDonThanhCong",listHoaDonThanhCong);
        model.addAttribute("listHoaDonCho",listHoaDonCho);
        model.addAttribute("listHoaDonHuy",listHoaDonHuy);
        model.addAttribute("hoaDonList",hoaDonList);
        return "manage/manage-bill-offline";
    }



}
