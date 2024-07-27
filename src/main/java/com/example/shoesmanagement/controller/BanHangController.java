package com.example.shoesmanagement.controller;
import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.repository.*;
import com.example.shoesmanagement.service.*;
import com.example.shoesmanagement.viewModel.GiayViewModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.SimpleDateFormat;
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

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    private int tongSanPham = 0;

    private double giaBan = 0;

    private double tongTienSanPham = tongSanPham * giaBan;

    private double giaTienGiam = 0;

    private double tongTien = tongTienSanPham - giaTienGiam;

    private UUID idHoaDon = null;

    private double dieuKienKhuyenMai = 0;

    @GetMapping("/hien-thi")
    public String hienThi(Model model
            , @ModelAttribute("messageSuccess") String messageSuccess
            , @ModelAttribute("messageError") String messageError, RedirectAttributes redirectAttributes) {
        List<GiayViewModel> list = giayViewModelService.getAllVm();

        NhanVien nhanVien = (NhanVien) httpSession.getAttribute("staffLogged");
        List<HoaDon> listHoaDonHomNay = hoaDonService.listAllHoaDonByNhanVienHienTai(nhanVien);
        model.addAttribute("listHoaDonHomNay", listHoaDonHomNay);

        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        List<KhuyenMai> khuyenMai = khuyenMaiService.getAllKhuyenMai();
        model.addAttribute("khuyenMai", khuyenMai);
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        model.addAttribute("tongTienSanPham", 0);
        model.addAttribute("tongTien", 0);
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
            Integer sequenceNumber = (Integer) session.getAttribute("sequenceNumber");
            if (sequenceNumber == null) {
                sequenceNumber = 1; // Khởi tạo nếu chưa có trong session
            }

            // Tạo mã hóa đơn với ngày hôm nay và số thứ tự
            SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
            String strDate = formatter.format(date);


            // Tăng số thứ tự và lưu lại trong session
            sequenceNumber++;
            session.setAttribute("sequenceNumber", sequenceNumber);
            hd.setMaHD("HD_" + strDate + "_" + sequenceNumber);
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
    public String chonHoaDon(@PathVariable("idHoaDon") UUID idHoaDon, Model model,
                             @ModelAttribute("messageSuccess") String messageSuccess,
                             @ModelAttribute("messageError") String messageError, RedirectAttributes redirectAttributes,
                             HttpSession httpSession) {

        NhanVien nhanVien = (NhanVien) httpSession.getAttribute("staffLogged");
        List<HoaDon> listHoaDonHomNay = hoaDonService.listAllHoaDonByNhanVienHienTai(nhanVien);
        model.addAttribute("listHoaDonHomNay", listHoaDonHomNay);

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

        httpSession.removeAttribute("idHoaDon");
        httpSession.setAttribute("idHoaDon", idHoaDon);

        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        model.addAttribute("gioHang", findByIdHoaDon);
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());

        if (findByIdHoaDon.isEmpty()) {
            model.addAttribute("messageGioHang", "Trong giỏ hàng chưa có sản phẩm");
        }
        double tongTienSanPham = hoaDonChiTietService.tongTienSanPham(findByIdHoaDon);
        double tongTien = hoaDonChiTietService.tongTien(findByIdHoaDon);

        // Giả sử KhuyenMai có phương thức getGiaTri() để lấy giá trị giảm giá
        KhuyenMai khuyenMai1 = hd.getKhuyenMai();
        double giaTienGiam = (khuyenMai1 != null && khuyenMai1.getGiaTienGiam() != null) ? khuyenMai1.getGiaTienGiam() : 0;

        // Nếu tổng tiền sản phẩm không đủ điều kiện khuyến mãi thì bỏ khuyến mãi
        if (tongTienSanPham < listKM.stream().mapToDouble(KhuyenMai::getDieuKienKMBill).min().orElse(Double.MAX_VALUE)) {
            giaTienGiam = 0;
        }
        model.addAttribute("tongTienSanPham", tongTienSanPham);
        model.addAttribute("tongTien", tongTien - giaTienGiam);
        model.addAttribute("giaTienGiam", giaTienGiam);
        model.addAttribute("listKhachHang", khachHangService.findKhachHangByTrangThai());
        model.addAttribute("tongSanPham", findByIdHoaDon.size());

        httpSession.setAttribute("tongSP", findByIdHoaDon.size());
        httpSession.setAttribute("tongTienSanPham", tongTienSanPham);
        httpSession.setAttribute("tongTien", tongTien - giaTienGiam);
        httpSession.setAttribute("giaTienGiam", giaTienGiam);

        // Cập nhật tổng tiền và tổng tiền sau khi giảm trong hóa đơn
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        hoaDon.setTongTienSanPham(tongTienSanPham);
        hoaDon.setTongTien(tongTien - giaTienGiam);
        hoaDonService.add(hoaDon);

        // Thông tin khách hàng
        KhachHang khachHang = hoaDon.getKhachHang();
        model.addAttribute("khachHang", khachHang != null ? khachHang : null);
        if (messageSuccess == null || !"true".equals(messageSuccess)) {
            model.addAttribute("messageSuccess", false);
        }
        if (messageError == null || !"true".equals(messageError)) {
            model.addAttribute("messageError", false);
        }
        model.addAttribute("idHoaDon", idHoaDon);
        return "/manage/ban-hang";
    }


    @GetMapping("/quet-qr/{idChiTietGiay}")
    public String quetQr(@PathVariable(value = "idChiTietGiay") UUID idChiTietGiay,
                         Model model, RedirectAttributes redirectAttributes) {
        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idChiTietGiay);

        UUID idHoaDon = (UUID) session.getAttribute("idHoaDon");
        if (idHoaDon == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
            return "redirect:/ban-hang/hien-thi";
        }
        else {

            if (chiTietGiay == null) {
                redirectAttributes.addFlashAttribute("messageError", true);
                redirectAttributes.addFlashAttribute("tbaoError", "Ảnh QR không đúng");
                return "redirect:/ban-hang/cart/hoadon/" + this.idHoaDon;
            }
            model.addAttribute("quetQR", chiTietGiay);
            model.addAttribute("idHoaDon", idHoaDon);
            model.addAttribute("tongTienSanPham", tongTienSanPham);
            model.addAttribute("tongTien", tongTienSanPham - giaTienGiam);

            model.addAttribute("showModalQuetQR", true);
        }

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
    public ResponseEntity<Map<String, Object>> updateQuantity(@RequestParam UUID idCTG, @RequestParam int quantity) {
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idCTG);
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idCTG);

        Map<String, Object> response = new HashMap<>();

        if (chiTietGiay == null || hoaDonChiTiet == null) {
            System.err.println("ChiTietGiay hoặc HoaDonChiTiet không tồn tại");
            response.put("error", "Sản phẩm không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (quantity > chiTietGiay.getSoLuong()) {
            response.put("error", "Số lượng trong kho không đủ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).get();
            hoaDon.setKhuyenMai(null);
            hoaDonRepository.saveAndFlush(hoaDon);

            int previousQuantity = hoaDonChiTiet.getSoLuong();
            hoaDonChiTiet.setSoLuong(quantity);
            hoaDonChiTiet.setDonGia(chiTietGiay.getGiaBan() * quantity);
            hoaDonChiTietService.add(hoaDonChiTiet);

            int quantityDifference = quantity - previousQuantity;
            chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() - quantityDifference);
            giayChiTietService.update(chiTietGiay);

            double tongTienSanPham = hoaDonService.getTongTienSanPham(idHoaDon);

            response.put("tongTienSanPham", tongTienSanPham);

            System.out.println("Updated HoaDonChiTiet: " + hoaDonChiTiet);
            System.out.println("Updated ChiTietGiay: " + chiTietGiay);

            return ResponseEntity.ok(response);
        }
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

//        KhuyenMai khuyenMai = hoaDon.getKhuyenMai();
//        if(khuyenMai != null){
//            khuyenMai.setSoLuong(khuyenMai.getSoLuong() - 1);
//            khuyenMai.setSoLuongDaDung(khuyenMai.getSoLuongDaDung() + 1);
//            khuyenMaiRepository.saveAndFlush(khuyenMai);
//        }

        if(hoaDon.getKhuyenMai() != null){   // nếu hoá đơn có dùng khuyến mãi
            KhuyenMai kmcsdl = khuyenMaiRepository.findById(hoaDon.getKhuyenMai().getIdKM()).get();
            int slmax = kmcsdl.getSoLuong();
            int sl = kmcsdl.getSoLuongDaDung();
            if(sl == slmax){    // nếu số lượng khuyến mãi đã hết thì xoá mã khuyến mãi ra khỏi hoá đơn và trả về trang thanh toán
                hoaDon.setKhuyenMai(null);
                hoaDonRepository.saveAndFlush(hoaDon);
//                return "redirect:/buyer/checkout?" + session.getAttribute("checkoutParams" + khachHang.getIdKH()).toString();
                return "redireact:/buyer/cart";
            } else if (sl < slmax) {    // nếu số lượng khuyến mãi nhỏ hơn sl đã set thì tăng sl lên 1
                kmcsdl.setSoLuongDaDung(sl + 1);
                khuyenMaiRepository.saveAndFlush(kmcsdl);
            }
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
        session.removeAttribute("khuyenMai");

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
        else {
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

        // Cập nhật tổng tiền hóa đơn
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        hoaDon.setTongTien(hoaDon.getTongTien() - hoaDonChiTiet.getDonGia());
        hoaDon.setTongTienSanPham(hoaDon.getTongTienSanPham() - hoaDonChiTiet.getDonGia());
        hoaDonService.add(hoaDon);

        // Cập nhật số lượng sản phẩm và trạng thái của ChiTietGiay
        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() + hoaDonChiTiet.getSoLuong());
        chiTietGiay.setTrangThai(1);
        giayChiTietService.save(chiTietGiay);

        // Xóa chi tiết hóa đơn
        hoaDonChiTietService.delete(hoaDonChiTiet);

        // Cập nhật số lượng sản phẩm trong giỏ hàng
        tongSanPham--;
        httpSession.setAttribute("tongSP", tongSanPham);

        httpSession.removeAttribute("idChiTietGiay");
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
        ;
        model.addAttribute("tongTien", hoaDonChiTietService.tongTien(findByIdHoaDon));
        ;
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


    public String generateRandomNumbers() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomNumber = random.nextInt(10); // Tạo số ngẫu nhiên từ 0 đến 9
            sb.append(randomNumber);
        }
        return sb.toString();
    }

    //
    @GetMapping("/chon-khuyen-mai/{idKM}")
    public String chonKM(Model model, @PathVariable("idKM") UUID idKM, RedirectAttributes redirectAttributes) {
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");

        if (idHoaDon == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
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
    @GetMapping("/delete-hoa-don-cho/{idHD}")
    public String deleteHoaDonChoByIdHD(@PathVariable UUID idHD, RedirectAttributes redirectAttributes) {
        try {
            hoaDonService.deleteHoaDonCho(idHD);
            session.removeAttribute("idHoaDon");
            session.removeAttribute("tongSP");
            session.removeAttribute("tongTien");
            session.removeAttribute("tongTienSanPham");
            session.removeAttribute("cart");
            session.removeAttribute("khuyenMai");
            session.removeAttribute("idChiTietGiay");
            redirectAttributes.addFlashAttribute("messageSuccess", true);
            redirectAttributes.addFlashAttribute("tb", "Hóa đơn đã được xóa thành công.");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Không tìm thấy hóa đơn với ID: " + idHD);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Có lỗi xảy ra, không thể xóa hóa đơn!");
        }
        return "redirect:/ban-hang/hien-thi";
    }
}

