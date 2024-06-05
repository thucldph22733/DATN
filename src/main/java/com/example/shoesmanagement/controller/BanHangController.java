package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.repository.KhachHangRepository;
import com.example.shoesmanagement.repository.KhuyenMaiRepository;
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

import java.util.*;
import java.util.Date;
import java.util.List;
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
    private HttpSession session;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    private double tongTienSanPham = 0;

//    private double tienKhuyenMai = 0;

    private double giaTienGiam = 0;

    private double tongTien = tongTienSanPham - giaTienGiam;

    private UUID idHoaDon = null;

    private int tongSanPham = 0;

    private double dieuKienKhuyenMai = 0;

    @GetMapping("/hien-thi")
    public String hienThi(Model model
            , @ModelAttribute("messageSuccess") String messageSuccess
            , @ModelAttribute("messageError") String messageError) {
        List<GiayViewModel> list = giayViewModelService.getAllVm();
        NhanVien nhanVien = (NhanVien) httpSession.getAttribute("staffLogged");
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

    @GetMapping("/cart/hoadon/{idHoaDon}")
    public String chonHoaDon(@PathVariable("idHoaDon") UUID idHoaDon, Model model
            , @ModelAttribute("messageSuccess") String messageSuccess
            , @ModelAttribute("messageError") String messageError
    ) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        List<KhuyenMai> khuyenMai = khuyenMaiService.getAllKhuyenMai();
        model.addAttribute("khuyenMai", khuyenMai);
        model.addAttribute("giaTienGiam", giaTienGiam);
        httpSession.removeAttribute("idHoaDon");
        httpSession.setAttribute("idHoaDon", idHoaDon);
        this.idHoaDon = idHoaDon;
        model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        if (findByIdHoaDon.isEmpty()) {
            model.addAttribute("messageGioHang", "Trong giỏ hàng chưa có sản phẩm");
        } else {
            model.addAttribute("gioHang", findByIdHoaDon);
        }
        model.addAttribute("tongTienSanPham", hoaDonChiTietService.tongTienSanPham(findByIdHoaDon));
        model.addAttribute("tongTien", hoaDonChiTietService.tongTien(findByIdHoaDon) - giaTienGiam);
        model.addAttribute("listKhachHang", khachHangService.findKhachHangByTrangThai());

        model.addAttribute("tongSanPham", findByIdHoaDon.size());
        httpSession.setAttribute("tongSP", findByIdHoaDon.size());
        httpSession.setAttribute("tongTienSanPham", hoaDonChiTietService.tongTienSanPham(findByIdHoaDon));
        httpSession.setAttribute("tongTien", hoaDonChiTietService.tongTien(findByIdHoaDon)-giaTienGiam);

        // add tong tièn
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        hoaDon.setTongTienSanPham(hoaDonChiTietService.tongTienSanPham(findByIdHoaDon));
        hoaDon.setTongTien(tongTien);
        hoaDonService.add(hoaDon);

        //khach hang
        KhachHang khachHang = hoaDon.getKhachHang();
        if (khachHang == null || khachHang.getIdKH() == null) {
            model.addAttribute("khachHang", null);
        } else {
            model.addAttribute("khachHang", httpSession.getAttribute("khachHang"));
        }
        if (messageSuccess == null || !"true".equals(messageSuccess)) {
            System.out.println(messageSuccess);
            model.addAttribute("messageSuccess", false);
        }
        if (messageError == null || !"true".equals(messageError)) {
            model.addAttribute("messageError", false);
        }
        model.addAttribute("idHoaDon",idHoaDon);
        return "/manage/ban-hang";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword, Model model,
                         RedirectAttributes redirectAttributes) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
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
            }
            else {
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

        List<HoaDonChiTiet> findByIdHoaDon = hoaDonChiTietService.findByIdHoaDon(idHoaDon);
        if (findByIdHoaDon.isEmpty()) {
            model.addAttribute("messageGioHang", "Trong giỏ hàng chưa có sản phẩm");
        }

        model.addAttribute("tongTienSanPham", hoaDonChiTietService.tongTienSanPham(findByIdHoaDon));
        model.addAttribute("gioHang", findByIdHoaDon);
        return "/manage/ban-hang";
    }

    @GetMapping("/chon-size/{idGiay}/{mauSac}")
    public String chonSize(@PathVariable(value = "idGiay") UUID idGiay,
                           @PathVariable(value = "mauSac") String mauSac, Model model) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        Giay giay = giayService.getByIdGiay(idGiay);
        List<ChiTietGiay> sizeList = sizeRepository.findByIdGiayAndMauSac2(idGiay, mauSac);
        model.addAttribute("giay", giay);
        model.addAttribute("listChiTietGiay", sizeList);
        model.addAttribute("idHoaDon", idHoaDon);
        model.addAttribute("showModal", true);

        model.addAttribute("tongTienSanPham", tongTienSanPham);
        model.addAttribute("tongTien", tongTien);
        return "/manage/ban-hang";
    }

    @GetMapping("/add-to-cart")
    public String addToCart(@RequestParam("idChiTietGiay") UUID idChiTietGiay,
                            @RequestParam("soLuong") int soLuong, Model model,
                            RedirectAttributes redirectAttributes) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        if (idHoaDon == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Bạn chưa chọn hóa đơn");
            model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
            return "redirect:/ban-hang/hien-thi";
        }
        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idChiTietGiay);
        if (soLuong > chiTietGiay.getSoLuong()) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Số lượng trong kho không đủ");
            model.addAttribute("listHoaDon", hoaDonService.getListHoaDonChuaThanhToan());
            return "redirect:/ban-hang/cart/hoadon/" + this.idHoaDon;
        }
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idChiTietGiay);
        if (hoaDonChiTiet != null) {
            hoaDonChiTiet.setDonGia(chiTietGiay.getGiaBan() * (hoaDonChiTiet.getSoLuong() + soLuong));
            hoaDonChiTiet.setSoLuong(hoaDonChiTiet.getSoLuong() + soLuong);
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
            httpSession.setAttribute("tongSP", tongSanPham);
            hoaDonChiTietService.add(hdct);
        }
        if (soLuong == chiTietGiay.getSoLuong()){
            chiTietGiay.setTrangThai(0);
        }
        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() - soLuong);
        giayChiTietService.save(chiTietGiay);
        redirectAttributes.addFlashAttribute("messageSuccess", true);
        redirectAttributes.addFlashAttribute("tb", "Thêm vào giỏ hàng thành công");
        return "redirect:/ban-hang/cart/hoadon/" + this.idHoaDon;
    }

    @GetMapping("/xoa-gio-hang/{idChiTietGiay}")
    public String xoaSanPham(@PathVariable("idChiTietGiay") UUID idChiTietGiay, RedirectAttributes redirectAttributes,Model model) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idChiTietGiay);
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idChiTietGiay);

        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() + hoaDonChiTiet.getSoLuong());
        chiTietGiay.setTrangThai(1);
        giayChiTietService.save(chiTietGiay);

        hoaDonChiTiet.setTrangThai(0);
        hoaDonChiTiet.setSoLuong(0);
        hoaDonChiTiet.setDonGia(0.0);
        hoaDonChiTietService.add(hoaDonChiTiet);
        tongSanPham--;
        httpSession.setAttribute("tongSP", tongSanPham);
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

        model.addAttribute("tongTienSanPham", hoaDonChiTietService.tongTienSanPham(findByIdHoaDon));;
        model.addAttribute("tongTien", hoaDonChiTietService.tongTien(findByIdHoaDon));;
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
    public String addKhachHang( @ModelAttribute("kh") KhachHang khachHang,Model model,RedirectAttributes redirectAttributes) {
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

    //thanh toan
    @GetMapping("/thanh-toan")
    public String thanhToan(RedirectAttributes redirectAttributes,Model model) {
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        this.tongSanPham = (int) httpSession.getAttribute("tongSP");
        this.tongTien = (double) httpSession.getAttribute("tongTien");
        this.tongTienSanPham = (double) httpSession.getAttribute("tongTienSanPham");
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        hoaDon.setTrangThai(1);
        hoaDon.setTgThanhToan(new Date());

        hoaDon.setTongTienSanPham(tongTienSanPham);
        hoaDon.setTongTien(tongTien);

        hoaDon.setTongSP(tongSanPham);
        hoaDon.setHinhThucThanhToan(0);

        hoaDonService.add(hoaDon);

        this.tongTienSanPham = 0;
        this.tongTien = 0;
        this.tongSanPham = 0;
        httpSession.removeAttribute("idHoaDon");
        httpSession.removeAttribute("khachHang");
        redirectAttributes.addFlashAttribute("messageSuccess", true);
        redirectAttributes.addFlashAttribute("tb", "Thanh toán thành công");
        return "redirect:/ban-hang/hien-thi";
    }

    @PostMapping("/updateQuantity")
    @ResponseBody
    public void updateQuantity(@RequestParam UUID idCTG, @RequestParam int quantity) {
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idCTG);
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idCTG);
        hoaDonChiTiet.setSoLuong(quantity);
        hoaDonChiTiet.setDonGia(chiTietGiay.getGiaBan() * quantity);
        hoaDonChiTietService.add(hoaDonChiTiet);
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

    @GetMapping("/chon-khuyen-mai/{idKM}")
    public String chonKM(Model model, @PathVariable("idKM") UUID idKM, RedirectAttributes redirectAttributes){
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(idKM).orElse(null);
        giaTienGiam = khuyenMai.getGiaTienGiam();

        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        hoaDon.setKhuyenMai(khuyenMai);
        hoaDonService.add(hoaDon);
        httpSession.setAttribute("khuyenMai", khuyenMai);
        redirectAttributes.addFlashAttribute("messageSuccess", true);
//        redirectAttributes.addFlashAttribute("tb", "Thêm khách hàng thành công");
        return "redirect:/ban-hang/cart/hoadon/" + idHoaDon;
    }
}
