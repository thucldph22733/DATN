package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.HoaDonChiTiet;
//import com.example.shoesmanagement.model.KhuyenMaiChiTietHoaDon;
import com.example.shoesmanagement.repository.KhuyenMaiRepository;
import com.example.shoesmanagement.service.*;
//import com.example.shoesmanagement.service.KhuyenMaiChiTietHoaDonService;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private GiayService giayService;

    @Autowired
    private GiayViewModelService giayViewModelService;

    @Autowired
    private HttpSession session;

    @GetMapping("/offline")
    public String getAll(Model model){

        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/login";
        }

        List<HoaDon> hoaDonList = hoaDonService.getAllHoaDonOffLine();

        List<HoaDon> listHoaDonThanhCong = new ArrayList<>();
        List<HoaDon> listHoaDonCho = new ArrayList<>();
        List<HoaDonChiTiet> hoaDonChiTietList = new ArrayList<>();

        int tongHoaDon = hoaDonList.size();
        Double soTienDaThanhToan = 0.0;
        int tongSanPham = 0;
        int soLuongHoaDonCho = 0;
        int soLuongHoaDonThanhCong = 0;
        int soLuongSanPhamDaBan = 0;

        for (HoaDon x: hoaDonList) {
            if (x.getTrangThai() == 0){
                soLuongHoaDonCho++;
                listHoaDonCho.add(x);
            }
            if (x.getTrangThai() == 1){
                soLuongHoaDonThanhCong++;
                soTienDaThanhToan += x.getTongTien();
                listHoaDonThanhCong.add(x);
                soLuongSanPhamDaBan += x.getTongSP();

                // Lấy danh sách chi tiết hóa đơn cho hóa đơn hiện tại
                List<HoaDonChiTiet> chiTietList = x.getHoaDonChiTiets();
                for (HoaDonChiTiet chiTiet : chiTietList) {
                    tongSanPham += chiTiet.getSoLuong();
                    hoaDonChiTietList.add(chiTiet);
                }
            }
        }

        model.addAttribute("soLuongHoaDonTaiQuay", tongHoaDon);
        model.addAttribute("tongTienBanTaiQuay", soTienDaThanhToan);
        model.addAttribute("tongSanPhamDaBan", soLuongSanPhamDaBan);
        model.addAttribute("tongSoLuongSanPham", tongSanPham);  // Tổng số lượng chi tiết sản phẩm
        model.addAttribute("hoaDonThanhCong", soLuongHoaDonThanhCong);
        model.addAttribute("hoaDonChoTaiQuay", soLuongHoaDonCho);

        model.addAttribute("listHoaDonThanhCong", listHoaDonThanhCong);
        model.addAttribute("listHoaDonCho", listHoaDonCho);
        model.addAttribute("hoaDonList", hoaDonList);
        model.addAttribute("hoaDonChiTietList", hoaDonChiTietList);  // Danh sách chi tiết hóa đơn

        return "manage/manage-bill-offline";
    }



    @GetMapping("/detail/{idHoaDon}")
    public String detailHoaDon(@PathVariable("idHoaDon")UUID idHoaDon, Model model){
        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        model.addAttribute("hoaDonChiTiet",findByIdHoaDon);
        model.addAttribute("hoaDon",hoaDon);
        model.addAttribute("modalHoaDon",true);
        return "manage/manage-bill-offline";
    }



}
