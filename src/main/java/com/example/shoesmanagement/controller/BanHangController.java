package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.repository.KhachHangRepository;
import com.example.shoesmanagement.repository.SizeRepository;
import com.example.shoesmanagement.service.*;
import com.example.shoesmanagement.viewModel.GiayViewModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

<<<<<<< HEAD
import java.util.*;
=======
import java.util.Date;
import java.util.List;
import java.util.UUID;

>>>>>>> 69b043a9a28dd24572c9324abe431fbde603da14
import java.util.UUID;


@Controller
@RequestMapping("/ban-hang")
public class BanHangController {
    @Autowired
    private HangService hangService;

    @Autowired
    private GiayChiTietService giayChiTietService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private GiayService giayService;

    @Autowired
    private GiayViewModelService giayViewModelService;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private HoaDonChiTIetService hoaDonChiTietService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private KhachHangRepository khachHangRepository;

    private double tongTien = 0;
    private double tienKhuyenMai = 0;
    private double tongTienSauKM = tongTien - tienKhuyenMai;
    private UUID idHoaDon = null;
    private int tongSanPham = 0;
    private double dieuKienKhuyenMai = 0;

    @GetMapping("/hien-thi")
    public String hienThi(Model model
            , @ModelAttribute("messageSuccess") String messageSuccess
            , @ModelAttribute("messageError") String messageError) {

        List<GiayViewModel> list = giayViewModelService.getAllVm();
        NhanVien nhanVien = (NhanVien) httpSession.getAttribute("staffLogged");
        model.addAttribute("listSanPham", list);

        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        model.addAttribute("tongTien", 0);
        model.addAttribute("tongSanPham", 0);
        model.addAttribute("khuyenMai", 0);
        model.addAttribute("khachHang", null);
        model.addAttribute("tongTienSauKM", 0);
        model.addAttribute("listKhachHang", khachHangService.findKhachHangByTrangThai());
        if (!"true".equals(messageSuccess)) {
            model.addAttribute("messageSuccess", false);
        }
        if (!"true".equals(messageError)) {
            model.addAttribute("messageError", false);
        }
        return "/manage/ban-hang";
    }

    @GetMapping("/add-cart")
    public String taoHoaDon(Model model, RedirectAttributes redirectAttributes) {
        NhanVien nhanVien = (NhanVien) httpSession.getAttribute("staffLogged");
        List<HoaDon> listHD = hoaDonService.getListHoaDonChuaThanhToan();
        if (listHD.size() < 3) {
            HoaDon hd = new HoaDon();
            Date date = new Date();
            hd.setMaHD("HD" + date.getDate() + generateRandomNumbers());
            hd.setTgTao(new Date());
            hd.setTrangThai(0);
            hd.setLoaiHD(1);
            hd.setNhanVien(nhanVien);
            hoaDonService.add(hd);
            model.addAttribute("message", "Tạo hóa đơn thành công");
            redirectAttributes.addFlashAttribute("messageSuccess", true);
            redirectAttributes.addFlashAttribute("tb", "Tạo thành công hóa đơn");
        } else {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Quá số lượng hóa đơn");
        }
        model.addAttribute("listHoaDon", listHD);
        return "redirect:/ban-hang/hien-thi";
    }

//    @GetMapping("/quet-qr/{idChiTietGiay}")
//    public String quetQr(@PathVariable(value = "idChiTietGiay") UUID idChiTietGiay,
//                         Model model, RedirectAttributes redirectAttributes) {
//        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idChiTietGiay);
//        if (chiTietGiay == null) {
//            redirectAttributes.addFlashAttribute("messageError", true);
//            redirectAttributes.addFlashAttribute("tbaoError", "Ảnh QR không đúng");
//            return "redirect:/ban-hang/cart/hoadon/" + this.idHoaDon;
//        }
//        model.addAttribute("quetQR", chiTietGiay);
//        model.addAttribute("idHoaDon", idHoaDon);
//        model.addAttribute("khuyenMai", this.tienKhuyenMai);
//        model.addAttribute("tongTienSauKM", tongTien - tienKhuyenMai);
//        model.addAttribute("tongTien", tongTien);
//
//        model.addAttribute("showModalQuetQR", true);
//        return "/manage/ban-hang";
//    }

    public String generateRandomNumbers() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomNumber = random.nextInt(10); // Tạo số ngẫu nhiên từ 0 đến 9
            sb.append(randomNumber);
        }
        return sb.toString();
    }



}
