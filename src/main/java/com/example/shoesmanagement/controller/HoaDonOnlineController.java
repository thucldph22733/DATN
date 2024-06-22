package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.ChucVu;
import com.example.shoesmanagement.model.GiaoHang;
import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.NhanVien;
import com.example.shoesmanagement.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@RequestMapping("/manage/bill/")
@Controller
public class HoaDonOnlineController {
    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private NhanVienService nhanVienService;
    @Autowired
    private GiaoHangService giaoHangService;


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;

    @Autowired
    private LSThanhToanService lsThanhToanService;


    @Autowired
    private ChucVuService chucVuService;
    @GetMapping("online")
    private String manageBillOnline(Model model) {
        model.addAttribute("reLoadPage", true);
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/login";
        }
        showData(model);
        showTab1(model);


        return "manage/manage-bill-online";
    }
    @GetMapping("online/delivery/{idHD}")
    private String editBillOnline(Model model, @PathVariable UUID idHD) {

        HoaDon billDelivery = hoaDonService.getOne(idHD);
        List<NhanVien> listNhanVienGiao = new ArrayList<>();

        ChucVu nvgh = chucVuService.findByMaCV("GH01");

        List<NhanVien> listNVGH = nhanVienService.findByChucVu(nvgh);

        if (listNVGH != null) {
            for (NhanVien x : listNVGH) {
                listNhanVienGiao.add(x);
            }
        }

        model.addAttribute("listNhanVienGiao", listNhanVienGiao);
        model.addAttribute("billDelivery", billDelivery);
        model.addAttribute("showEditBillDelivery", true);

        showTab3(model);

        showData(model);


        return "manage/manage-bill-online";
    }
    @PostMapping("online/delivery/confirm/{idHD}")
    private String confirmNVGH(Model model, @PathVariable UUID idHD) {

        UUID idNV = UUID.fromString(request.getParameter("idNhanVien"));

        Date date = new Date();
        NhanVien nhanVien = nhanVienService.getByIdNhanVien(idNV);
        HoaDon hoaDon = hoaDonService.getOne(idHD);

        hoaDon.setNhanVien(nhanVien);
        hoaDon.setTrangThai(2);

        hoaDonService.add(hoaDon);

        GiaoHang giaoHang = hoaDon.getGiaoHang();
        giaoHang.setNoiDung("Xác nhận nhân viên giao hàng");
        giaoHang.setTgShip(date);
        giaoHangService.saveGiaoHang(giaoHang);

        hoaDon.setGiaoHang(giaoHang);
        hoaDonService.add(hoaDon);



        showData(model);

        model.addAttribute("showMessThanhCong", true);
        model.addAttribute("reLoadPage", true);
        showData(model);
        showTab3(model);


        return "manage/manage-bill-online";
    }

    private void showTab1(Model model){
        model.addAttribute("activeAll", "nav-link active");
        model.addAttribute("xac_nhan_tt", "nav-link");
        model.addAttribute("van_chuyen", "nav-link");

        model.addAttribute("tabpane1", "tab-pane show active");
        model.addAttribute("tabpane2", "tab-pane");
        model.addAttribute("tabpane3", "tab-pane");
        model.addAttribute("tabpane4", "tab-pane");

    }
    private void showTab3(Model model){

        model.addAttribute("activeAll", "nav-link");
        model.addAttribute("xac_nhan_tt", "nav-link");
        model.addAttribute("van_chuyen", "nav-link active");


        model.addAttribute("tabpane1", "tab-pane");
        model.addAttribute("tabpane2", "tab-pane");
        model.addAttribute("tabpane3", "tab-pane show active");
        model.addAttribute("tabpane4", "tab-pane");
    }
    private void showData(Model model){

        List<HoaDon> listAllHoaDonOnline = hoaDonService.listHoaDonOnline();


            List<HoaDon> listAllHoaDonDangGiao = new ArrayList<>();
        List<HoaDon> listHoaDonOnlineQRCode = new ArrayList<>();



        int soLuongHoaDonOnline = 0;
        int soLuongHoaDonHuy = 0;
        int soLuongHoaDonDaThanhToan = 0 ;
        int soLuongHoaDonChuaThanhToanNhanHang = 0 ;
        int soLuongHoaDonDangGiao = 0;
        int soLuongHoaDonBanking = 0;
        int soLuongHoaDonDaNhan = 0;

        int soLuongHoaDonThanhToanKhiNhanHang = 0;

        double tongTienHoaDon = 0.0;
        if (listAllHoaDonOnline != null) {
            for (HoaDon x : listAllHoaDonOnline) {
                if (x.getTrangThai() == 6 || x.getTrangThai() == 7) {
                    System.out.println("abc");
                } else {
                    tongTienHoaDon += x.getTongTien();
                }
            }
        }

        if (listAllHoaDonOnline != null){
            for (HoaDon x: listAllHoaDonOnline) {
                if (x.getTrangThai() == 6 || x.getTrangThai() == 7){
                    System.out.println("abc");
                }else{
                    if(x.getTrangThai() == 4){
                        soLuongHoaDonHuy ++;
                    }
                    if (x.getHinhThucThanhToan() == 1 ){
                        soLuongHoaDonBanking ++;
                        listHoaDonOnlineQRCode.add(x);
                    }
                    if (x.getHinhThucThanhToan() == 0){
                        soLuongHoaDonThanhToanKhiNhanHang ++;
                    }

                    if (x.getTrangThai() == 1 && x.getHinhThucThanhToan() == 1 ){
                        soLuongHoaDonDaThanhToan ++;
                    }
                    if (x.getTrangThai() == 3 && x.getHinhThucThanhToan() == 0 ){
                        soLuongHoaDonDaThanhToan ++;
                    }
                    if (x.getTrangThai() == 2 ){
                        soLuongHoaDonDangGiao ++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 1){
                        soLuongHoaDonDangGiao ++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 4){
                        soLuongHoaDonHuy ++;
                    }
                    if (x.getTrangThai() == 3){
                        soLuongHoaDonDaNhan ++;
                    }
                }
            }
        }

        int soLuongHoaDonChuaThanhToan =  soLuongHoaDonChuaThanhToanNhanHang +  soLuongHoaDonBanking ;


        int soLuongHdHoanChoXacNhan = 0;
        int soLuongHdHoanKhachHuy = 0;



        model.addAttribute("sumBillOnline", soLuongHoaDonOnline);
        model.addAttribute("totalAmount", tongTienHoaDon);
        model.addAttribute("sumQuantityBaking", soLuongHoaDonBanking);
        model.addAttribute("sumQuantityDelivery", soLuongHoaDonThanhToanKhiNhanHang);
        model.addAttribute("hoaDonChuaThanhToan", soLuongHoaDonChuaThanhToan);
        model.addAttribute("hoaDonDaThanhToan", soLuongHoaDonDaThanhToan);
        model.addAttribute("hoaDonDangGiao", soLuongHoaDonDangGiao);
        model.addAttribute("hoaDonHuy", soLuongHoaDonHuy);
        model.addAttribute("hoaDonDaNhan", soLuongHoaDonDaNhan);

        model.addAttribute("soLuongHdHoanChoXacNhan", soLuongHdHoanChoXacNhan);
        model.addAttribute("soLuongHdHoanKhachHuy", soLuongHdHoanKhachHuy);

        model.addAttribute("listHoaDonOnline", listAllHoaDonOnline);
        model.addAttribute("listHoaDonOnlineGiaoHang", listAllHoaDonDangGiao);
    }

}
