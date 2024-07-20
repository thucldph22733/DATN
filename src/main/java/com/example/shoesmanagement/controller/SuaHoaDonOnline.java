package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.repository.*;
import com.example.shoesmanagement.service.*;
import com.example.shoesmanagement.viewModel.GiayViewModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequestMapping("/manage/changehd/")
@Controller
public class SuaHoaDonOnline {

    private int tongSanPham = 0;

    private double giaBan = 0;

    private double tongTienSanPham = tongSanPham * giaBan;

    private double giaTienGiam = 0;

    private double tongTien = tongTienSanPham - giaTienGiam;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private GiayService giayService;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private MauSacService mauSacService;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private GiayRepository giayRepository;
    @Autowired
    private HinhAnhRepository hinhAnhRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private HangRepository hangRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;
    @Autowired
    private GiayViewModelService giayViewModelService;

    @Autowired
    private LSThanhToanService lsThanhToanService;
    @Autowired
    private GiayChiTietService giayChiTietService;

    @Autowired
    private HoaDonChiTIetService hoaDonChiTietService;

    @Autowired
    private ChucVuService chucVuService;
    @Autowired
    private HttpSession httpSession;

    @GetMapping("/online/{idHD}")
    private String manageBillOnline(@PathVariable UUID idHD, Model model, HttpSession session) {
        // Kiểm tra quyền đăng nhập
        if (session.getAttribute("managerLogged") == null) {
            return "redirect:/login";
        }

        // Đưa id của hóa đơn vào session
        session.setAttribute("idHoaDon", idHD);

        // Đưa id của hóa đơn vào model để sử dụng trong trang sua_hd_online
        model.addAttribute("idHD", idHD);

        // Lấy dữ liệu cần thiết và đưa vào model
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        List<ChiTietGiay> chiTietGiayList = new ArrayList<>();
        List<Giay> giayList = giayService.getAllGiay();
        List<Size> sizeList = sizeService.getAllSize();
        List<MauSac> mauSacList = mauSacService.getALlMauSac();

        // Lấy hóa đơn cụ thể dựa vào idHD
        HoaDon hoaDon = hoaDonService.getOne(idHD);
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDon.getHoaDonChiTiets();

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("hoaDonChiTiets", hoaDonChiTiets);
        model.addAttribute("items", chiTietGiayList);
        model.addAttribute("giayList", giayList);
        model.addAttribute("sizeList", sizeList);
        model.addAttribute("mauSacList", mauSacList);
        model.addAttribute("listSanPham", listG);
        model.addAttribute("tongSP", hoaDon.getTongSP());
        model.addAttribute("tongTien", hoaDon.getTongTien());
        model.addAttribute("trangThai", hoaDon.getTrangThai());
        model.addAttribute("khachHang", hoaDon.getKhachHang());

        showData(model);
        showTab1(model);

        return "manage/sua_hd_online";
    }


    private void showData(Model model) {

        List<HoaDon> listAllHoaDonOnline = hoaDonService.listHoaDonOnline();


        List<HoaDon> listAllHoaDonDangGiao = new ArrayList<>();
        List<HoaDon> listHoaDonOnlineBaking = new ArrayList<>();


        int soLuongHoaDonOnline = 0;
        int soLuongHoaDonHuy = 0;
        int soLuongHoaDonDaThanhToan = 0;
        int soLuongHoaDonChuaThanhToanNhanHang = 0;
        int soLuongHoaDonDangGiao = 0;
        int soLuongHoaDonBanking = 0;
        int soLuongHoaDonDaNhan = 0;
        int soLuongHoaDonChoXacNhan = 0;
        int soLuongHoaDonChoLayHang = 0;


        int soLuongHoaDonThanhToanKhiNhanHang = 0;

        double tongTienHoaDon = 0.0;
        if (listAllHoaDonOnline != null) {
            for (HoaDon x : listAllHoaDonOnline) {
                if (x.getTrangThai() == 6 || x.getTrangThai() == 7) {
//                    System.out.println("abc");
                } else {
                    tongTienHoaDon += x.getTongTien();
                }
            }
        }

        if (listAllHoaDonOnline != null) {
            for (HoaDon x : listAllHoaDonOnline) {
                if (x.getTrangThai() == 6 || x.getTrangThai() == 7) {
//                    System.out.println("abc");
                } else {
                    if (x.getHinhThucThanhToan() == 1) {
                        soLuongHoaDonBanking++;
                        listHoaDonOnlineBaking.add(x);
                    }
                    if (x.getHinhThucThanhToan() == 0) {
                        soLuongHoaDonThanhToanKhiNhanHang++;
                    }

                    if (x.getTrangThai() == 1 && x.getHinhThucThanhToan() == 1) {
                        soLuongHoaDonDaThanhToan++;
                    }
                    if (x.getTrangThai() == 4 && x.getHinhThucThanhToan() == 0) {
                        soLuongHoaDonDaThanhToan++;
                    }
                    if (x.getTrangThai() == 1) {
                        soLuongHoaDonChoXacNhan++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 2) {
                        soLuongHoaDonChoLayHang++;
                    }
                    if (x.getTrangThai() == 3) {
                        soLuongHoaDonDangGiao++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 3) {
                        soLuongHoaDonDangGiao++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 5) {
                        soLuongHoaDonHuy++;
                    }
                    if (x.getTrangThai() == 4) {
                        soLuongHoaDonDaNhan++;
                    }
                }
            }
        }

        int soLuongHoaDonChuaThanhToan = soLuongHoaDonChuaThanhToanNhanHang + soLuongHoaDonBanking;


        int soLuongHdHoanChoXacNhan = 0;
        int soLuongHdHoanKhachHuy = 0;


        model.addAttribute("sumBillOnline", soLuongHoaDonOnline);
        model.addAttribute("totalAmount", tongTienHoaDon);
        model.addAttribute("sumQuantityBaking", soLuongHoaDonBanking);
        model.addAttribute("sumQuantityDelivery", soLuongHoaDonThanhToanKhiNhanHang);
        model.addAttribute("hoaDonChuaThanhToan", soLuongHoaDonChuaThanhToan);
        model.addAttribute("hoaDonDaThanhToan", soLuongHoaDonDaThanhToan);
        model.addAttribute("hoaDonDangGiao", soLuongHoaDonDangGiao);
        model.addAttribute("hoaDonChoXacNhan", soLuongHoaDonChoXacNhan);
        model.addAttribute("hoaDonChoLayHang", soLuongHoaDonChoLayHang);
        model.addAttribute("hoaDonHuy", soLuongHoaDonHuy);
        model.addAttribute("hoaDonDaNhan", soLuongHoaDonDaNhan);

        model.addAttribute("soLuongHdHoanChoXacNhan", soLuongHdHoanChoXacNhan);
        model.addAttribute("soLuongHdHoanKhachHuy", soLuongHdHoanKhachHuy);

        model.addAttribute("listHoaDonOnline", listAllHoaDonOnline);
        model.addAttribute("listHoaDonOnlineGiaoHang", listAllHoaDonDangGiao);
    }
    private void showTab1(Model model) {
        model.addAttribute("activeAll", "nav-link active");
        model.addAttribute("xac_nhan_tt", "nav-link");
        model.addAttribute("van_chuyen", "nav-link");

        model.addAttribute("tabpane1", "tab-pane show active");
        model.addAttribute("tabpane2", "tab-pane");
        model.addAttribute("tabpane3", "tab-pane");
        model.addAttribute("tabpane4", "tab-pane");

    }

    @GetMapping("/chon-size2/{idGiay}/{mauSac}")
    public String chonSize(@PathVariable(value = "idGiay") UUID idGiay,
                           @PathVariable(value = "mauSac") String mauSac, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        UUID idHoaDon = (UUID) session.getAttribute("idHoaDon");

        // Kiểm tra nếu idHoaDon bị null và xử lý
        if (idHoaDon == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
            return "redirect:/manage/bill/online";
        }

        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        model.addAttribute("idHoaDon", idHoaDon);
        Giay giay = giayService.getByIdGiay(idGiay);
        List<ChiTietGiay> sizeList = sizeRepository.findByIdGiayAndMauSac2(idGiay, mauSac);

        model.addAttribute("gioHang", hoaDonChiTietService.findByIdHoaDon(idHoaDon));
        model.addAttribute("giay", giay);
        model.addAttribute("listChiTietGiay", sizeList);
        model.addAttribute("showModal1", true);
        model.addAttribute("tongTienSanPham", tongTienSanPham);
        model.addAttribute("tongTien", tongTien);

        return "/manage/sua_hd_online";
    }
    @GetMapping("/add-to-hd/{idHD}")
    public String addToCart(@PathVariable("idHD") UUID idHoaDon,
                            @RequestParam("idChiTietGiay") UUID idChiTietGiay,
                            @RequestParam("soLuong") int soLuong, Model model,
                            RedirectAttributes redirectAttributes, HttpSession session) {
        if (idHoaDon == null || idChiTietGiay == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Giá trị idHoaDon hoặc idChiTietGiay bị thiếu");
            return "redirect:/manage/changehd/online/";
        }

        System.out.println("idHoaDon: " + idHoaDon);
        System.out.println("idChiTietGiay: " + idChiTietGiay);
        System.out.println("soLuong: " + soLuong);

        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);

        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idChiTietGiay);
        if (soLuong > chiTietGiay.getSoLuong()) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Số lượng trong kho không đủ");
            return "redirect:/manage/changehd/online/" + idHoaDon;
        }

        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        model.addAttribute("idHoaDon", idHoaDon);

        List<HoaDonChiTiet> cart = (List<HoaDonChiTiet>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idChiTietGiay);
        model.addAttribute("hdct", hoaDonChiTiet);
        if (hoaDonChiTiet != null) {
            // Giữ nguyên đơn giá, chỉ cập nhật số lượng
            hoaDonChiTiet.setSoLuong(hoaDonChiTiet.getSoLuong() + soLuong);
            hoaDonChiTiet.setTrangThai(1);
            hoaDonChiTietService.add(hoaDonChiTiet);
        } else {
            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct.setChiTietGiay(chiTietGiay);
            hdct.setHoaDon(hoaDon);
            hdct.setDonGia(chiTietGiay.getGiaBan());
            hdct.setSoLuong(soLuong);
            hdct.setTrangThai(1);
            hdct.setTgThem(new Date());
            tongSanPham++;
            session.setAttribute("tongSP", tongSanPham);
            hoaDonChiTietService.add(hdct);
            cart.add(hdct);
        }

        hoaDon.setTongTienSanPham(hoaDon.getTongTienSanPham() + chiTietGiay.getGiaBan() * soLuong);
        giayChiTietService.save(chiTietGiay);
        hoaDonService.save(hoaDon);
        hoaDonService.updateHoaDon(hoaDon);

        redirectAttributes.addFlashAttribute("messageSuccess", true);
        redirectAttributes.addFlashAttribute("tb", "Thêm vào giỏ hàng thành công");
        return "redirect:/manage/changehd/online/" + idHoaDon;
    }
}
