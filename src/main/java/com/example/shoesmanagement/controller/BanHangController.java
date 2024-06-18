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

import java.util.*;

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
    private HttpSession session;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;


    @Autowired
    private MauSacService mauSacService;


    private int tongSanPham = 0;

    private final double giaBan = 0;

    private double tongTienSanPham = tongSanPham * giaBan;

    private UUID idHoaDon = null;
    private double tongTien = 0;


    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    private final double dieuKienKhuyenMai = 0;

    private double giaTienGiam = 0;

    @GetMapping("/hien-thi")
    public String hienThi(Model model
            , @ModelAttribute("messageSuccess") String messageSuccess
            , @ModelAttribute("messageError") String messageError, RedirectAttributes redirectAttributes) {
        List<GiayViewModel> list = giayViewModelService.getAllVm();
        NhanVien nhanVien = (NhanVien) httpSession.getAttribute("staffLogged");
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        List<KhuyenMai> khuyenMai = khuyenMaiService.getAllKhuyenMai();
        model.addAttribute("khuyenMai", khuyenMai);
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        model.addAttribute("tongTienSanPham", 0);
        model.addAttribute("tongTien", 0);
//        model.addAttribute("danhSachMau", danhSachMau);
        model.addAttribute("tongSanPham", 0);
        model.addAttribute("khachHang", null);
        model.addAttribute("listKhachHang", khachHangService.findKhachHangByTrangThai());
        if (!"true".equals(messageSuccess)) {
            model.addAttribute("messageSuccess", false);
        }
        if (!"true".equals(messageError)) {
            model.addAttribute("messageError", false);
        }
        if (session.getAttribute("staffLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "/login";
        }
        return "/manage/ban-hang";
    }

    @GetMapping("/add-cart")
    public String taoHoaDon(Model model, RedirectAttributes redirectAttributes) {
        NhanVien nhanVien = (NhanVien) httpSession.getAttribute("staffLogged");
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        List<HoaDon> listHD = hoaDonService.getListHoaDonChuaThanhToan();
        List<KhuyenMai> khuyenMai = khuyenMaiService.getAllKhuyenMai();
        model.addAttribute("khuyenMai", khuyenMai);
        if (listHD.size() < 5) {
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
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chỉ được tạo tối đa 5 hóa đơn");
        }
        model.addAttribute("listHoaDon", listHD);
        return "redirect:/ban-hang/hien-thi";
    }

    @GetMapping("/cart/hoadon/{idHoaDon}")
    public String chonHoaDon(@PathVariable("idHoaDon") UUID idHoaDon, Optional<UUID> idKM, Model model
            , @ModelAttribute("messageSuccess") String messageSuccess
            , @ModelAttribute("messageError") String messageError, RedirectAttributes redirectAttributes
    ) {
        if (idHoaDon == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
            model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
            return "redirect:/ban-hang/hien-thi";
        }
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        List<KhuyenMai> khuyenMai = khuyenMaiService.getAllKhuyenMai();
        model.addAttribute("khuyenMai", khuyenMai);
        HoaDon hd = hoaDonService.getOne(idHoaDon);
        double tongTienSP = hd.getTongTienSanPham() != null ? hd.getTongTienSanPham() : 0;
        List<KhuyenMai> listKM = hoaDonRepository.listDieuKienKhuyenMai(tongTienSP);
        model.addAttribute("dieuKienKhuyenMai", listKM);

//        double giaTienGiam = 0.0;
//        if (idKM.isPresent()) {
//            KhuyenMai voucher = khuyenMaiRepository.findById(idKM.get()).get();
//            giaTienGiam = voucher.getGiaTienGiam();
//            hd.setKhuyenMai(voucher);
//        }
        model.addAttribute("giaTienGiam", giaTienGiam);

        httpSession.removeAttribute("idHoaDon");
        httpSession.setAttribute("idHoaDon", idHoaDon);
        this.idHoaDon = idHoaDon;

        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        model.addAttribute("gioHang", findByIdHoaDon);
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());

        if (findByIdHoaDon.isEmpty()) {
            model.addAttribute("messageGioHang", "Trong giỏ hàng chưa có sản phẩm");
        } else {
            model.addAttribute("gioHang", findByIdHoaDon);
        }
        model.addAttribute("tongTienSanPham", hd.getTongTienSanPham());
        model.addAttribute("tongTien", hd.getTongTien());
        hd.setTongTien(tongTienSanPham-giaTienGiam);
        model.addAttribute("listKhachHang", khachHangService.findKhachHangByTrangThai());

        model.addAttribute("tongSanPham", findByIdHoaDon.size());
        httpSession.setAttribute("tongSP", findByIdHoaDon.size());

        httpSession.setAttribute("tongTienSanPham", hd.getTongTienSanPham());
        httpSession.setAttribute("giaTienGiam", hd.getKhuyenMai());

        httpSession.setAttribute("tongTienSanPham", hoaDonChiTietService.tongTienSanPham(findByIdHoaDon));
        httpSession.setAttribute("tongTien", hd.getTongTien());

//         httpSession.setAttribute("tongTien", hoaDonChiTietService.tongTien(findByIdHoaDon) - giaTienGiam);

//        httpSession.setAttribute("tongTien", hd.getTongTien());

        // add tổng tiền và cập nhật hóa đơn
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        hoaDon.setTongTienSanPham(hoaDonChiTietService.tongTienSanPham(findByIdHoaDon));
        hoaDon.setTongTien(tongTien);
        hoaDonService.add(hoaDon);


        //khách hàng
        KhachHang khachHang = hoaDon.getKhachHang();
        if (khachHang == null || khachHang.getIdKH() == null) {
            model.addAttribute("khachHang", null);
        } else {
            model.addAttribute("khachHang", httpSession.getAttribute("khachHang"));
        }
        if (!"true".equals(messageSuccess)) {
            System.out.println(messageSuccess);
            model.addAttribute("messageSuccess", false);
        }
        if (!"true".equals(messageError)) {
            model.addAttribute("messageError", false);
        }
        model.addAttribute("idHoaDon", idHoaDon);
        return "/manage/ban-hang";
    }

    @GetMapping("/chon-size/{idGiay}/{mauSac}")
    public String chonSize(@PathVariable(value = "idGiay") UUID idGiay,
                           @PathVariable(value = "mauSac") String mauSac, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        UUID idHoaDon = (UUID) session.getAttribute("idHoaDon");
        if (idHoaDon == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
            return "redirect:/ban-hang/hien-thi";
        }

        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        Giay giay = giayService.getByIdGiay(idGiay);
        List<ChiTietGiay> sizeList = sizeRepository.findByIdGiayAndMauSac2(idGiay, mauSac);

        model.addAttribute("gioHang", hoaDonChiTietService.findByIdHoaDon(idHoaDon));
        model.addAttribute("giay", giay);
        model.addAttribute("listChiTietGiay", sizeList);
        model.addAttribute("idHoaDon", idHoaDon);
        model.addAttribute("showModal", true);
        model.addAttribute("tongTienSanPham", tongTienSanPham);
        model.addAttribute("tongTien", tongTien);

        return "/manage/ban-hang";
    }

    @PostMapping("/updateQuantity")
    @ResponseBody
    public void updateQuantity(@RequestParam UUID idCTG, @RequestParam int quantity) {
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idCTG);
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idCTG);

        if (chiTietGiay == null || hoaDonChiTiet == null) {
            // Log lỗi nếu không tìm thấy thông tin
            System.err.println("ChiTietGiay or HoaDonChiTiet not found");
            return;
        }

        // Cập nhật số lượng và đơn giá trong hóa đơn chi tiết
        int previousQuantity = hoaDonChiTiet.getSoLuong();
        hoaDonChiTiet.setSoLuong(quantity);
        hoaDonChiTiet.setDonGia(chiTietGiay.getGiaBan() * quantity);
        hoaDonChiTietService.add(hoaDonChiTiet);

        // Cập nhật số lượng trong kho sản phẩm
        int quantityDifference = quantity - previousQuantity;
        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() - quantityDifference);
        giayChiTietService.update(chiTietGiay);  // Giả sử phương thức update đã được định nghĩa trong giayChiTietService

        // Log để kiểm tra
        System.out.println("Updated HoaDonChiTiet: " + hoaDonChiTiet);
        System.out.println("Updated ChiTietGiay: " + chiTietGiay);
    }

    @GetMapping("/add-to-cart")
    public String addToCart(@RequestParam("idChiTietGiay") UUID idChiTietGiay,
                            @RequestParam("soLuong") int soLuong, Model model,
                            RedirectAttributes redirectAttributes, HttpSession session) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        UUID idHoaDon = (UUID) session.getAttribute("idHoaDon");

        if (idHoaDon == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
            return "redirect:/ban-hang/hien-thi";
        }

        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idChiTietGiay);
        if (soLuong > chiTietGiay.getSoLuong()) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Số lượng trong kho không đủ");
            return "redirect:/ban-hang/cart/hoadon/" + idHoaDon;
        }

        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        List<HoaDonChiTiet> cart = (List<HoaDonChiTiet>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idChiTietGiay);
        if (hoaDonChiTiet != null) {
            hoaDonChiTiet.setDonGia(chiTietGiay.getGiaBan() * (hoaDonChiTiet.getSoLuong() + soLuong));
            hoaDonChiTiet.setSoLuong(hoaDonChiTiet.getSoLuong() + soLuong);
            hoaDon.setTongTienSanPham(chiTietGiay.getGiaBan() * soLuong);
            hoaDonChiTiet.setTrangThai(1);
            hoaDonChiTietService.add(hoaDonChiTiet);
        } else {
            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct.setChiTietGiay(chiTietGiay);
            hdct.setHoaDon(hoaDon);
            hdct.setDonGia(chiTietGiay.getGiaBan() * soLuong);
            hdct.setSoLuong(soLuong);
            hdct.setTrangThai(1);
            hdct.setTgThem(new Date());
            tongSanPham++;
            hoaDon.setTongTienSanPham(chiTietGiay.getGiaBan() * soLuong);

            session.setAttribute("tongSP", tongSanPham);

            hoaDonChiTietService.add(hdct);
            cart.add(hdct);
        }

        if (soLuong == chiTietGiay.getSoLuong()) {
            chiTietGiay.setTrangThai(0);
        }

        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() - soLuong);
        giayChiTietService.save(chiTietGiay);

        // Update tổng tiền sản phẩm trong hóa đơn
        hoaDon.setTongTienSanPham(hoaDon.getTongTienSanPham() + chiTietGiay.getGiaBan() * soLuong);
        hoaDonService.save(hoaDon);

        redirectAttributes.addFlashAttribute("messageSuccess", true);
        redirectAttributes.addFlashAttribute("tb", "Thêm vào giỏ hàng thành công");
        return "redirect:/ban-hang/cart/hoadon/" + idHoaDon;
    }

    @GetMapping("/thanh-toan")
    public String thanhToan(@RequestParam(value = "idCTG", required = false) UUID idCTG, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        if (idCTG != null) {
            ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idCTG);
            model.addAttribute("chiTietGiay", chiTietGiay);
        }


        this.tongSanPham = (int) session.getAttribute("tongSP");
        this.tongTien = (double) session.getAttribute("tongTien");
        this.tongTienSanPham = (double) session.getAttribute("tongTienSanPham");
        UUID idHoaDon = (UUID) session.getAttribute("idHoaDon");
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);

        if (listG == null || listG.isEmpty()) {
            redirectAttributes.addFlashAttribute("messageSuccess", true);
            redirectAttributes.addFlashAttribute("tb", "Chưa có sản phẩm trong giỏ hàng");
            return "redirect:/ban-hang/hien-thi";
        }

        hoaDon.setTrangThai(1);
        hoaDon.setTgThanhToan(new Date());
        hoaDon.setTongTienSanPham(tongTienSanPham);
        hoaDon.setTongTien(tongTien);
        hoaDon.setTongSP(tongSanPham);
        hoaDon.setHinhThucThanhToan(0);

        KhuyenMai khuyenMai = hoaDon.getKhuyenMai();
        if(khuyenMai != null){
            khuyenMai.setSoLuong(khuyenMai.getSoLuong() - 1);
            khuyenMai.setSoLuongDaDung(khuyenMai.getSoLuongDaDung() + 1);
            khuyenMaiRepository.saveAndFlush(khuyenMai);
        }
        hoaDonService.add(hoaDon);

        this.tongTienSanPham = 0;
        this.tongTien = 0;
        this.tongSanPham = 0;

        session.removeAttribute("idHoaDon");
        session.removeAttribute("khachHang");
        session.removeAttribute("tongSP");
        session.removeAttribute("tongTien");
        session.removeAttribute("tongTienSanPham");
        session.removeAttribute("cart");

        redirectAttributes.addFlashAttribute("messageSuccess", true);
        redirectAttributes.addFlashAttribute("tb", "Thanh toán thành công");

        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        return "redirect:/ban-hang/hien-thi";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword, Model model,
                         RedirectAttributes redirectAttributes) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        model.addAttribute("gioHang", findByIdHoaDon);
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        if (idHoaDon == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
            model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
            return "redirect:/ban-hang/hien-thi";
        }

        if (keyword.length() >= 3 && keyword.substring(0, 3).equals("CTG")) {
            if (idHoaDon == null) {
                redirectAttributes.addFlashAttribute("messageError", true);
                redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
                model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
                return "redirect:/ban-hang/hien-thi";
            } else {
                ChiTietGiay chiTietGiay = giayChiTietService.findByMa(keyword);
                if (chiTietGiay == null) {
                    redirectAttributes.addFlashAttribute("messageError", true);
                    redirectAttributes.addFlashAttribute("tbaoError", "Không tìm thấy sản mã phẩm ");
                    return "redirect:/ban-hang/cart/hoadon/" + this.idHoaDon;
                }
                model.addAttribute("quetQR", chiTietGiay);
                model.addAttribute("idHoaDon", idHoaDon);
                model.addAttribute("showModalQuetQR", true);
            }
        } else {
            List<GiayViewModel> list = giayViewModelService.getAll(keyword);
            if (list.isEmpty()) {
                redirectAttributes.addFlashAttribute("messageError", true);
                redirectAttributes.addFlashAttribute("tbaoError", "Không tìm thấy sản phẩm");
                return "redirect:/ban-hang/cart/hoadon/" + this.idHoaDon;
            }
            model.addAttribute("listSanPham", list);
        }


        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());

        if (findByIdHoaDon.isEmpty()) {
            model.addAttribute("messageGioHang", "Trong giỏ hàng chưa có sản phẩm");
        }

        model.addAttribute("tongTienSanPham", hoaDonChiTietService.tongTienSanPham(findByIdHoaDon));
        model.addAttribute("gioHang", findByIdHoaDon);
        return "/manage/ban-hang";
    }


    @GetMapping("/xoa-gio-hang/{idChiTietGiay}")
    public String xoaSanPham(@PathVariable("idChiTietGiay") UUID idChiTietGiay, RedirectAttributes redirectAttributes, Model model) {
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idChiTietGiay);
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idChiTietGiay);
        giaTienGiam = 0;
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        hoaDon.setTongTien(hoaDon.getTongTien() - hoaDonChiTiet.getDonGia());
        hoaDon.setTongTienSanPham(hoaDon.getTongTienSanPham() - hoaDonChiTiet.getDonGia());
        hoaDonService.add(hoaDon);

        giaTienGiam = 0;

        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() + hoaDonChiTiet.getSoLuong());
        chiTietGiay.setTrangThai(1);
        giayChiTietService.save(chiTietGiay);

        hoaDonChiTiet.setTrangThai(0);
        hoaDonChiTiet.setSoLuong(0);
        hoaDonChiTiet.setDonGia(0.0);
        hoaDonChiTietService.add(hoaDonChiTiet);
        tongSanPham--;

        httpSession.setAttribute("tongSP", tongSanPham);
        httpSession.setAttribute("tongTienSanPham", hoaDon.getTongTienSanPham());
        httpSession.setAttribute("tongTien", hoaDon.getTongTien());
        httpSession.removeAttribute("idHoaDon");

        redirectAttributes.addFlashAttribute("giaTienGiam", hoaDon.getTongTienSanPham());
        redirectAttributes.addFlashAttribute("tongTien", hoaDon.getTongTien());
        redirectAttributes.addFlashAttribute("messageSuccess", true);
        redirectAttributes.addFlashAttribute("tb", "Xóa thành công");
        return "redirect:/ban-hang/cart/hoadon/" + idHoaDon;
    }

    @GetMapping("/list-khach-hang")
    public String listKH(Model model) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        model.addAttribute("gioHang", findByIdHoaDon);
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        model.addAttribute("listKhachHang", khachHangService.findKhachHangByTrangThai());
        model.addAttribute("idHoaDon", idHoaDon);
        model.addAttribute("showModalKhachHang", true);
        model.addAttribute("tongTienSanPham", hoaDonChiTietService.tongTienSanPham(findByIdHoaDon));
        model.addAttribute("tongTien", hoaDonChiTietService.tongTien(findByIdHoaDon));
        return "/manage/ban-hang";
    }

    @GetMapping("/chon-khach-hang/{idKhachHang}")
    public String chonKhachHang(@PathVariable("idKhachHang") UUID idKhachHang, Model model) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        model.addAttribute("gioHang", findByIdHoaDon);
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        KhachHang khachHang = khachHangService.getByIdKhachHang(idKhachHang);
        httpSession.setAttribute("khachHang", khachHang);
        model.addAttribute("khachHang", khachHang);
        //update khách hàng
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        hoaDon.setKhachHang(khachHang);
        hoaDonService.add(hoaDon);
        return "redirect:/ban-hang/cart/hoadon/" + idHoaDon;
    }

    @GetMapping("/search-khach-hang")
    public String searchKhachHang(@RequestParam(value = "keyword", required = false) String keyword,
                                  Model model, RedirectAttributes redirectAttributes) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        model.addAttribute("gioHang", findByIdHoaDon);
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        model.addAttribute("showModalKhachHang", true);
        List<KhachHang> search = khachHangService.findKhachHangByKeyword(keyword);
        if (search.isEmpty()) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Không tìm thấy khách hàng");
            return "redirect:/ban-hang/cart/hoadon/" + idHoaDon;
        }
        model.addAttribute("listKhachHang", search);
        model.addAttribute("idHoaDon", idHoaDon);
        model.addAttribute("tongTienSanPham", tongTienSanPham);
        model.addAttribute("tongTien", tongTien);
        model.addAttribute("khuyenMai", this.giaTienGiam);
        return "/manage/ban-hang";
    }

    @GetMapping("/khach-hang/viewAdd")
    public String viewAdd(Model model) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        model.addAttribute("gioHang", findByIdHoaDon);
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        model.addAttribute("idHoaDon", idHoaDon);
        model.addAttribute("tongTienSanPham", tongTienSanPham);
        model.addAttribute("addKH", new KhachHang());
        model.addAttribute("showModalAddKH", true);
        return "/manage/ban-hang";
    }

    @PostMapping("/khach-hang/add")
    public String addKhachHang(@ModelAttribute("kh") KhachHang khachHang, Model model, RedirectAttributes redirectAttributes) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        if (khachHangRepository.existsBySdtKH(khachHang.getSdtKH())) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Đã tồn tại số điện thoại");
            model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
            return "redirect:/ban-hang/cart/hoadon/" + this.idHoaDon;
        } else {
            String ma = generateRandomNumbers();
            khachHang.setMaKH("KH" + ma);
            khachHang.setTrangThai(1);
            khachHang.setTgThem(new Date());

            khachHangService.addKhachHang(khachHang);
//            // up date kh vào hóa đơn
            UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
            HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
            hoaDon.setKhachHang(khachHang);
            hoaDonService.add(hoaDon);
            httpSession.setAttribute("khachHang", khachHang);
            redirectAttributes.addFlashAttribute("messageSuccess", true);
            redirectAttributes.addFlashAttribute("tb", "Thêm khách hàng thành công");
            return "redirect:/ban-hang/cart/hoadon/" + idHoaDon;
        }
    }

//    @GetMapping("/thanh-toan")
//    public String thanhToan(@RequestParam(value = "idCTG", required = false) UUID idCTG, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
//
//        List<GiayViewModel> listG = giayViewModelService.getAllVm();
//        model.addAttribute("listSanPham", listG);
//        if (idCTG != null) {
//            ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idCTG);
//            model.addAttribute("chiTietGiay", chiTietGiay);
//        }
//
//
//        this.tongSanPham = (int) session.getAttribute("tongSP");
//        this.tongTien = (double) session.getAttribute("tongTien");
//        this.tongTienSanPham = (double) session.getAttribute("tongTienSanPham");
//        UUID idHoaDon = (UUID) session.getAttribute("idHoaDon");
//        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
//
//        if (listG == null || listG.isEmpty()) {
//            redirectAttributes.addFlashAttribute("messageSuccess", true);
//            redirectAttributes.addFlashAttribute("tb", "Chưa có sản phẩm trong giỏ hàng");
//            return "redirect:/ban-hang/hien-thi";
//        }
//
//        hoaDon.setTrangThai(1);
//        hoaDon.setTgThanhToan(new Date());
//        hoaDon.setTongTienSanPham(tongTienSanPham);
//        hoaDon.setTongTien(tongTien);
//        hoaDon.setTongSP(tongSanPham);
//        hoaDon.setHinhThucThanhToan(0);
//
//        KhuyenMai khuyenMai = hoaDon.getKhuyenMai();
//        khuyenMai.setSoLuong(khuyenMai.getSoLuong() - hoaDon.getTongSP());
//        khuyenMai.setSoLuongDaDung(khuyenMai.getSoLuongDaDung() + hoaDon.getTongSP());
//        hoaDonService.add(hoaDon);
//
//        this.tongTienSanPham = 0;
//        this.tongTien = 0;
//        this.tongSanPham = 0;
//
//        session.removeAttribute("idHoaDon");
//        session.removeAttribute("khachHang");
//        session.removeAttribute("tongSP");
//        session.removeAttribute("tongTien");
//        session.removeAttribute("tongTienSanPham");
//        session.removeAttribute("cart");
//
//        redirectAttributes.addFlashAttribute("messageSuccess", true);
//        redirectAttributes.addFlashAttribute("tb", "Thanh toán thành công");
//
//        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
//        return "redirect:/ban-hang/hien-thi";
//    }

//    @PostMapping("/updateQuantity")
//    @ResponseBody
//    public void updateQuantity(@RequestParam UUID idCTG, @RequestParam int quantity) {
//        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
//        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idCTG);
//        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idCTG);
//
//        if (chiTietGiay == null || hoaDonChiTiet == null) {
//            // Log lỗi nếu không tìm thấy thông tin
//            System.err.println("ChiTietGiay or HoaDonChiTiet not found");
//            return;
//        }
//
//        // Cập nhật số lượng và đơn giá trong hóa đơn chi tiết
//        int previousQuantity = hoaDonChiTiet.getSoLuong();
//        hoaDonChiTiet.setSoLuong(quantity);
//        hoaDonChiTiet.setDonGia(chiTietGiay.getGiaBan() * quantity);
//        hoaDonChiTietService.add(hoaDonChiTiet);
//
//        // Cập nhật số lượng trong kho sản phẩm
//        int quantityDifference = quantity - previousQuantity;
//        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() - quantityDifference);
//        giayChiTietService.update(chiTietGiay);  // Giả sử phương thức update đã được định nghĩa trong giayChiTietService
//
//        // Log để kiểm tra
//        System.out.println("Updated HoaDonChiTiet: " + hoaDonChiTiet);
//        System.out.println("Updated ChiTietGiay: " + chiTietGiay);
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

    @GetMapping("/chon-khuyen-mai/{idKM}")
    public String chonKM(Model model, @PathVariable("idKM") UUID idKM, RedirectAttributes redirectAttributes) {
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        if (idHoaDon == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
//            model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
            return "redirect:/ban-hang/hien-thi";
        }
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(idKM).orElse(null);
        if (khuyenMai != null) {
            UUID idHoaDon1 = (UUID) httpSession.getAttribute("idHoaDon");
            HoaDon hoaDon = hoaDonService.getOne(idHoaDon1);
            giaTienGiam = khuyenMai.getGiaTienGiam();
            hoaDon.setTongTien(hoaDon.getTongTienSanPham() - khuyenMai.getGiaTienGiam());
            hoaDon.setKhuyenMai(khuyenMai);
            hoaDonService.add(hoaDon);
        }
        redirectAttributes.addFlashAttribute("messageSuccess", true);
        return "redirect:/ban-hang/cart/hoadon/" + idHoaDon;
    }
}