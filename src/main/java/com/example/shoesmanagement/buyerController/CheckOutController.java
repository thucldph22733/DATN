package com.example.shoesmanagement.buyerController;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.repository.HoaDonRepository;
import com.example.shoesmanagement.repository.KhuyenMaiRepository;
import com.example.shoesmanagement.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class CheckOutController {
    @Autowired
    private HttpSession session;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private HoaDonChiTIetService hoaDonChiTietService;

    @Autowired
    private GiayChiTietService giayChiTietService;

    @Autowired
    private DiaChiKHService diaChiKHService;

    @Autowired
    private GHCTService ghctService;

    @Autowired
    private HttpServletRequest request;


    @Autowired
    private ShippingFeeService shippingFeeService;

    @Autowired
    private LSThanhToanService lsThanhToanService;

    @Autowired
    private GiaoHangService giaoHangService;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private KhuyenMaiService khuyenMaiService;


    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HttpSession httpSession;

    private double giaTienGiam = 0;

    @GetMapping("/buyer/checkout")

    private String checkOutCart(Model model, HttpServletRequest request,
                                @RequestParam("selectedProducts") List<String> selectedProducts,
                                @RequestParam(name = "productIds") List<UUID> productIds,
                                @RequestParam(name = "quantities") List<Integer> quantities,
                                Optional<UUID> idKM,
                                RedirectAttributes redirectAttribute) {
        Map<UUID, Integer> selectedProduct = new HashMap<>();
        for (int i = 0; i < selectedProducts.size(); i++) {
            if (!selectedProducts.get(i).equals("none")) selectedProduct.put(productIds.get(i), quantities.get(i));
        }


        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");

        DiaChiKH diaChiKHDefault = diaChiKHService.findDCKHDefaulByKhachHang(khachHang);
        List<DiaChiKH> diaChiKHList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, false, 1);

        List<HoaDonChiTiet> listHDCTCheckOut = new ArrayList<>();
        Date date = new Date();
        HoaDon hoaDon = new HoaDon();

        List<KhuyenMai> khuyenMai = khuyenMaiService.getAllKhuyenMai();
        model.addAttribute("khuyenMai", khuyenMai);


        String maHD = "HD_" + khachHang.getMaKH() + "_" + date.getDate() + generateRandomNumbers();
        session.setAttribute("checkoutParams" + khachHang.getIdKH(), request.getQueryString());
        hoaDon.setKhachHang(khachHang);
        hoaDon.setMaHD(maHD);
        hoaDon.setLoaiHD(0);
        hoaDon.setTgTao(date);
        hoaDon.setTrangThai(6);
        hoaDonService.add(hoaDon);

        GiaoHang giaoHang = new GiaoHang();
        giaoHang.setHoaDon(hoaDon);
        giaoHangService.saveGiaoHang(giaoHang);
        hoaDon.setGiaoHang(giaoHang);
        hoaDonService.add(hoaDon);

        if (diaChiKHDefault != null) {
            hoaDon.setDiaChiNguoiNhan(diaChiKHDefault.getDiaChiChiTiet());
            hoaDon.setSdtNguoiNhan(diaChiKHDefault.getSdtNguoiNhan());
            hoaDon.setTenNguoiNhan(diaChiKHDefault.getTenNguoiNhan());
            session.removeAttribute("diaChiGiaoHang");
            session.setAttribute("diaChiGiaoHang", diaChiKHDefault);

            giaoHang.setTenNguoiNhan(diaChiKHDefault.getTenNguoiNhan());
            giaoHang.setMaGiaoHang("");
            giaoHang.setSdtNguoiNhan(diaChiKHDefault.getSdtNguoiNhan());
            giaoHang.setDiaChiNguoiNhan(diaChiKHDefault.getDiaChiChiTiet());

            giaoHangService.saveGiaoHang(giaoHang);
            hoaDonService.add(hoaDon);
        }

        double total = 0.0;
        for (Map.Entry<UUID, Integer> entry : selectedProduct.entrySet()) {
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            GioHangChiTiet gioHangChiTiet = ghctService.findByCTGActiveAndKhachHangAndTrangThai(giayChiTietService.getByIdChiTietGiay(entry.getKey()), gioHang);
            ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(entry.getKey());

//            if (gioHangChiTiet.getSoLuong() > chiTietGiay.getSoLuong()) {
//                redirectAttribute.addFlashAttribute("successMessage",
//                        "Số lượng sản phẩm hiện còn: " + chiTietGiay.getSoLuong() + " đôi. Vui lòng giảm số lượng");
//                String idGiay = String.valueOf(chiTietGiay.getGiay().getIdGiay());
//                String idMau = String.valueOf(chiTietGiay.getMauSac().getIdMau());
//                String linkBack = idGiay + "/" +idMau;
//                return "redirect:/buyer/cart" ;
//            }

            if (entry.getValue() > chiTietGiay.getSoLuong()) {
                redirectAttribute.addFlashAttribute("successMessage", "Số lượng sản phẩm hiện còn: " + chiTietGiay.getSoLuong() + " đôi. Vui lòng giảm số lượng");
                String idGiay = String.valueOf(chiTietGiay.getGiay().getIdGiay());
                String idMau = String.valueOf(chiTietGiay.getMauSac().getIdMau());
                String linkBack = idGiay + "/" + idMau;
                return "redirect:/buyer/shop-details/" + linkBack;
            } else {
                // Xử lý trường hợp số lượng trong giỏ hàng lớn hơn số lượng tồn
                // Có thể bắn lỗi, thông báo cho người dùng hoặc xử lý theo cách khác
                hoaDonChiTiet.setHoaDon(hoaDon);
                hoaDonChiTiet.setChiTietGiay(chiTietGiay);
                hoaDonChiTiet.setDonGia(chiTietGiay.getGiaBan());
                hoaDonChiTiet.setSoLuong(entry.getValue());
                hoaDonChiTiet.setTgThem(new Date());
                hoaDonChiTiet.setTrangThai(1);
                hoaDonChiTietService.add(hoaDonChiTiet);
                listHDCTCheckOut.add(hoaDonChiTiet);
                total += (entry.getValue() * chiTietGiay.getGiaBan());
            }
        }

        double giaTienGiam = 0.0;
        if (idKM.isPresent()) {
            KhuyenMai voucher = khuyenMaiRepository.findById(idKM.get()).get();
            giaTienGiam = voucher.getGiaTienGiam();
            hoaDon.setKhuyenMai(voucher);
        }

        int sumQuantity = listHDCTCheckOut.stream()
                .mapToInt(HoaDonChiTiet::getSoLuong)
                .sum();

        List<KhuyenMai> listKM = hoaDonRepository.listDieuKienKhuyenMai(total);
        model.addAttribute("giaTienGiam", giaTienGiam);
        model.addAttribute("dieuKienKhuyenMai", listKM);

        hoaDon.setTongSP(sumQuantity);
        hoaDon.setTongTienSanPham(total);

        hoaDonService.add(hoaDon);

        model.addAttribute("sumQuantity", sumQuantity);
        model.addAttribute("total", total);
        model.addAttribute("listProductCheckOut", listHDCTCheckOut);
        model.addAttribute("toTalOder", total);

        if (diaChiKHDefault != null) {
            Double shippingFee = shippingFeeService.calculatorShippingFee(hoaDon, 25000.0);
            hoaDon.setTongTien(total + shippingFee - giaTienGiam);
            hoaDon.setTienShip(shippingFee);
            hoaDonService.add(hoaDon);

            giaoHang.setDiaChiNguoiNhan(diaChiKHDefault.getDiaChiChiTiet());
            giaoHang.setSdtNguoiNhan(diaChiKHDefault.getSdtNguoiNhan());
            giaoHang.setTenNguoiNhan(diaChiKHDefault.getTenNguoiNhan());
            giaoHangService.saveGiaoHang(giaoHang);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, shippingFeeService.tinhNgayNhanDuKien(diaChiKHDefault.getDiaChiChiTiet()));
            Date ngayDuKien = calendar.getTime();
            model.addAttribute("ngayDuKien", ngayDuKien);

            model.addAttribute("shippingFee", shippingFee);
            model.addAttribute("billPlaceOrder", hoaDon);

            model.addAttribute("toTalOder", total + shippingFee - giaTienGiam);

            model.addAttribute("tongTienDaGiamVoucherShip", total + shippingFee);
            model.addAttribute("diaChiKHDefault", diaChiKHDefault);
            model.addAttribute("addNewAddressNotNull", true);
            model.addAttribute("listAddressKH", diaChiKHList);
            // Tính toán ngày giao hàng dự kiến
//            LocalDate estimatedDeliveryDate = deliveryTimeService.calculateDeliveryDate(diaChiKHDefault.getDiaChiChiTiet());
//            model.addAttribute("estimatedDeliveryDate", estimatedDeliveryDate);

        } else {
//            model.addAttribute("tongTienDaGiamVoucherShip", total);
            model.addAttribute("addNewAddressNulll", true);
            model.addAttribute("addNewAddressNull", true);
        }


        session.removeAttribute("hoaDonTaoMoi");

        session.setAttribute("hoaDonTaoMoi", hoaDon);

        showData(model);
        return "online/checkout";
    }

    @PostMapping("/buyer/checkout/add/address")
    public String addNewAddressPlaceOrder(Model model, @RequestParam(name = "defaultSelected", defaultValue = "false") boolean defaultSelected) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        HoaDon hoaDon = (HoaDon) session.getAttribute("hoaDonTaoMoi");
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);

        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietService.findByHoaDon(hoaDon);
        List<DiaChiKH> diaChiKHList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, false, 1);

        Integer sumProductInCart = listGHCTActive.size();
        Date date = new Date();

        if (defaultSelected) {
            for (DiaChiKH xxx : diaChiKHService.getAllDiaChiKH()) {
                xxx.setLoai(false);
                diaChiKHService.save(xxx);
            }
        }

        String nameAddress = request.getParameter("nameAddress");
        String fullName = request.getParameter("fullName");
        String phoneAddress = request.getParameter("phoneAddress");
        String city = request.getParameter("city");
        String district = request.getParameter("district");
        String ward = request.getParameter("ward");
        String description = request.getParameter("description");
        String diaChiChiTiet = description + ", " + ward + ", " + district + ", " + city;

        DiaChiKH diaChiKH = new DiaChiKH();

        diaChiKH.setDiaChiChiTiet(diaChiChiTiet);
        diaChiKH.setMoTa(description);
        diaChiKH.setKhachHang(khachHang);
        diaChiKH.setTrangThai(1);
        diaChiKH.setMaDC("DC_" + khachHang.getMaKH() + date.getDay() + generateRandomNumbers());
        diaChiKH.setSdtNguoiNhan(phoneAddress);
        diaChiKH.setQuanHuyen(district);
        diaChiKH.setTenDC(nameAddress);
        diaChiKH.setTinhTP(city);
        diaChiKH.setTenNguoiNhan(fullName);
        diaChiKH.setXaPhuong(ward);
        diaChiKH.setTgThem(new Date());
        diaChiKH.setLoai(defaultSelected);

        diaChiKHService.save(diaChiKH);
        hoaDon.setDiaChiNguoiNhan(diaChiChiTiet);
        hoaDon.setTenNguoiNhan(fullName);
        hoaDon.setSdtNguoiNhan(phoneAddress);
        hoaDonService.add(hoaDon);

        int sumQuantity = hoaDonChiTietList.stream()
                .mapToInt(HoaDonChiTiet::getSoLuong)
                .sum();

        double total = hoaDonChiTietList.stream()
                .mapToDouble(HoaDonChiTiet::getDonGia)
                .sum();

        Double shippingFee = shippingFeeService.calculatorShippingFee(hoaDon, 25000.0);
        hoaDon.setTongTien(total + shippingFee - giaTienGiam);
        hoaDonService.add(hoaDon);

        GiaoHang giaoHang = hoaDon.getGiaoHang();
        giaoHang.setDiaChiNguoiNhan(diaChiKH.getDiaChiChiTiet());
        giaoHang.setSdtNguoiNhan(diaChiKH.getSdtNguoiNhan());
        giaoHang.setTenNguoiNhan(diaChiKH.getTenNguoiNhan());
        giaoHangService.saveGiaoHang(giaoHang);

        hoaDon.setTienShip(shippingFee);
        hoaDonService.add(hoaDon);

        Double shippingFee2 = shippingFeeService.calculatorShippingFee(hoaDon, 25000.0);

        model.addAttribute("sumQuantity", sumQuantity);
        model.addAttribute("total", total);
        model.addAttribute("diaChiKHDefault", diaChiKH);
        model.addAttribute("listProductCheckOut", hoaDonChiTietList);
        model.addAttribute("listAddressKH", diaChiKHList);
        model.addAttribute("fullNameLogin", khachHang.getHoTenKH());
        model.addAttribute("sumProductInCart", sumProductInCart);

        model.addAttribute("addNewAddressNotNull", true);
        model.addAttribute("billPlaceOrder", hoaDon);
        model.addAttribute("shippingFee", shippingFee2);
        model.addAttribute("toTalOder", total + shippingFee - giaTienGiam);

        session.removeAttribute("diaChiGiaoHang");
        session.setAttribute("diaChiGiaoHang", diaChiKH);
        showData(model);
        return "online/checkout";
    }

    @PostMapping("/buyer/checkout/change/address")
    private String changeAddressCheckOut(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        HoaDon hoaDon = (HoaDon) session.getAttribute("hoaDonTaoMoi");
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");

        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietService.findByHoaDon(hoaDon);
        List<DiaChiKH> diaChiKHList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, false, 1);
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);

        UUID idDCKH = UUID.fromString(request.getParameter("idDCKH"));
        DiaChiKH diaChiKHChange = diaChiKHService.findByIdDiaChiKH(idDCKH);

        GiaoHang giaoHang = hoaDon.getGiaoHang();

        giaoHang.setDiaChiNguoiNhan(diaChiKHChange.getDiaChiChiTiet());
        giaoHang.setSdtNguoiNhan(diaChiKHChange.getSdtNguoiNhan());
        giaoHang.setTenNguoiNhan(diaChiKHChange.getTenNguoiNhan());
        giaoHangService.saveGiaoHang(giaoHang);

        int sumQuantity = hoaDonChiTietList.stream()
                .mapToInt(HoaDonChiTiet::getSoLuong)
                .sum();
        double total = hoaDonChiTietList.stream()
                .mapToDouble(HoaDonChiTiet::getDonGia)
                .sum();

        hoaDonService.add(hoaDon);

        Double shippingFee = shippingFeeService.calculatorShippingFee(hoaDon, 25000.0);

        hoaDon.setTongTien(total + shippingFee - giaTienGiam);
        hoaDon.setTienShip(shippingFee);
        hoaDonService.add(hoaDon);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, shippingFeeService.tinhNgayNhanDuKien(diaChiKHChange.getDiaChiChiTiet()));
        Date ngayDuKien = calendar.getTime();
        model.addAttribute("ngayDuKien", ngayDuKien);

        model.addAttribute("sumQuantity", sumQuantity);
        model.addAttribute("total", total);
        model.addAttribute("diaChiKHDefault", diaChiKHChange);
        model.addAttribute("fullNameLogin", khachHang.getHoTenKH());

        model.addAttribute("listProductCheckOut", hoaDonChiTietList);
        model.addAttribute("listAddressKH", diaChiKHList);
        model.addAttribute("addNewAddressNotNull", true);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("billPlaceOrder", hoaDon);
        model.addAttribute("toTalOder", total + shippingFee - giaTienGiam);

        session.removeAttribute("diaChiGiaoHang");
        session.setAttribute("diaChiGiaoHang", diaChiKHChange);

        showData(model);

        return "online/checkout";
    }

    @PostMapping("/buyer/checkout/placeoder")
    public String placeOrder(Model model) {

        HoaDon hoaDon = (HoaDon) session.getAttribute("hoaDonTaoMoi");
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");

        String hinhThucThanhToan = request.getParameter("hinhThucThanhToan");
        String loiNhan = request.getParameter("loiNhan");

        Double shippingFee = shippingFeeService.calculatorShippingFee(hoaDon, 25000.0);

        Date ngayBatDau = hoaDon.getTgTao();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ngayBatDau);

        calendar.add(Calendar.DATE, shippingFeeService.tinhNgayNhanDuKien(hoaDon.getGiaoHang().getDiaChiNguoiNhan()));

        Date ngayKetThuc = calendar.getTime();

        GiaoHang giaoHang = hoaDon.getGiaoHang();

        hoaDon.setLoiNhan(loiNhan);
        hoaDon.setTgNhanDK(ngayKetThuc);


        KhuyenMai khuyenMai = hoaDon.getKhuyenMai();
        if (khuyenMai != null) {
            khuyenMai.setSoLuong(khuyenMai.getSoLuong() - 1);
            khuyenMai.setSoLuongDaDung(khuyenMai.getSoLuongDaDung() + 1);
            khuyenMaiRepository.saveAndFlush(khuyenMai);
        }

        hoaDonService.add(hoaDon);

        hoaDon.setTienShip(shippingFee);

        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietService.findByHoaDon(hoaDon);

        for (HoaDonChiTiet xx : hoaDonChiTietList) {
            GioHangChiTiet gioHangChiTiet = ghctService.findByCTSPActiveAndTrangThai(xx.getChiTietGiay(), 1);
            if (gioHangChiTiet != null) {
                gioHangChiTiet.setTrangThai(0);
                ghctService.addNewGHCT(gioHangChiTiet);
            } else {
                // Xử lý trường hợp gioHangChiTiet là null
            }

            ChiTietGiay chiTietGiay = xx.getChiTietGiay();

            int a = chiTietGiay.getSoLuong() - xx.getSoLuong();
            if (a == 0 || a <= 0) {
                chiTietGiay.setTrangThai(0);
            }

            chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() - xx.getSoLuong());
            giayChiTietService.save(chiTietGiay);
        }

        if (hinhThucThanhToan.equals("QRCodeBanking")) {

            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            double doubleNumber = hoaDon.getTongTien();
            int total = (int) doubleNumber;
            String vnpayUrl = vnPayService.createOrder(total, "orderInfo", baseUrl);

            hoaDon.setHinhThucThanhToan(1);
            hoaDon.setTrangThai(0);

            hoaDonService.add(hoaDon);

            LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
            lichSuThanhToan.setTgThanhToan(new Date());
            lichSuThanhToan.setSoTienThanhToan(hoaDon.getTongTien());
            lichSuThanhToan.setNoiDungThanhToan("Đặt hàng " + hoaDon.getMaHD() + " hình thức thanh toán VNPAY");
            lichSuThanhToan.setKhachHang(khachHang);
            lichSuThanhToan.setHoaDon(hoaDon);
            lichSuThanhToan.setMaLSTT("LSTT" + khachHang.getMaKH() + generateRandomNumbers());
            lichSuThanhToan.setTrangThai(0);
            lsThanhToanService.addLSTT(lichSuThanhToan);

            return "redirect:" + vnpayUrl;

        } else {
            hoaDon.setHinhThucThanhToan(0);
            hoaDon.setTrangThai(1);

            hoaDonService.add(hoaDon);

            LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
            lichSuThanhToan.setTgThanhToan(new Date());
            lichSuThanhToan.setSoTienThanhToan(hoaDon.getTongTien());
            lichSuThanhToan.setNoiDungThanhToan("Đặt hàng " + hoaDon.getMaHD() + " hình thức thanh toán khi nhận hàng");
            lichSuThanhToan.setKhachHang(khachHang);
            lichSuThanhToan.setHoaDon(hoaDon);
            lichSuThanhToan.setMaLSTT("LSTT" + khachHang.getMaKH() + generateRandomNumbers());
            lichSuThanhToan.setTrangThai(0);
            lsThanhToanService.addLSTT(lichSuThanhToan);

            GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
            List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
            List<HoaDon> listHoaDonByKhachHang = hoaDonService.findHoaDonByKhachHang(khachHang);
            List<HoaDon> listHoaDonChoThanhToan = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 0);
            Integer sumProductInCart = listGHCTActive.size();

            model.addAttribute("fullNameLogin", khachHang.getHoTenKH());
            model.addAttribute("sumProductInCart", sumProductInCart);
            model.addAttribute("pagePurchaseUser", true);
            model.addAttribute("purchaseAll", true);
            model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);
            model.addAttribute("listHoaDonChoThanhToan", listHoaDonChoThanhToan);
            model.addAttribute("type1", "active");

            model.addAttribute("modalDeliverySuccess", true);

            return "online/user";
        }
    }

    //    Delete
    @GetMapping("/buyer/shop/buyNowButton")
    private String buyNow(@RequestParam("idDetailProduct") UUID idDProduct, @RequestParam("quantity") int quantity, Model model, RedirectAttributes redirectAttribute) {

        ChiTietGiay ctg = giayChiTietService.getByIdChiTietGiay(idDProduct);

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");

        GioHangChiTiet gioHangChiTiet = ghctService.findByCTGActiveAndKhachHangAndTrangThai(ctg, gioHang);

        if (quantity > ctg.getSoLuong()) {
            String idGiay = String.valueOf(ctg.getGiay().getIdGiay());
            String idMau = String.valueOf(ctg.getMauSac().getIdMau());
            String linkBack = idGiay + "/" + idMau;
            redirectAttribute.addFlashAttribute("successMessage",
                    "Số lượng sản phẩm hiện còn: " + ctg.getSoLuong() + " đôi. Vui lòng giảm số lượng");
            return "redirect:/buyer/shop-details/" + linkBack;
        }


        if (gioHangChiTiet == null) {
            gioHangChiTiet = new GioHangChiTiet();
            gioHangChiTiet.setChiTietGiay(ctg);
            gioHangChiTiet.setGioHang(gioHang);
            gioHangChiTiet.setDonGia(ctg.getGiaBan() * quantity);
            gioHangChiTiet.setSoLuong(quantity);
            gioHangChiTiet.setTrangThai(1);
            gioHangChiTiet.setChiTietGiay(ctg);
            gioHangChiTiet.setTgThem(new Date());
            ghctService.addNewGHCT(gioHangChiTiet);
        } else {
            gioHangChiTiet.setDonGia(gioHangChiTiet.getDonGia() + ctg.getGiaBan() * quantity);
            gioHangChiTiet.setSoLuong(quantity + gioHangChiTiet.getSoLuong());
            gioHangChiTiet.setTrangThai(1);
            gioHangChiTiet.setChiTietGiay(ctg);
            gioHangChiTiet.setTgThem(new Date());
            ghctService.addNewGHCT(gioHangChiTiet);
        }

        DiaChiKH diaChiKHDefault = diaChiKHService.findDCKHDefaulByKhachHang(khachHang);
        List<DiaChiKH> diaChiKHList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, false, 1);

        Date date = new Date();
        HoaDon hoaDon = new HoaDon();

        String maHD = "HD_" + khachHang.getMaKH() + "_" + date.getDate() + generateRandomNumbers();

        hoaDon.setKhachHang(khachHang);
        hoaDon.setMaHD(maHD);
        hoaDon.setLoaiHD(0);
        hoaDon.setTgTao(date);
        hoaDon.setTrangThai(6);
        hoaDon.setLoiNhan(hoaDon.getLoiNhan());

        KhuyenMai khuyenMai = hoaDon.getKhuyenMai();
        if (khuyenMai != null) {
            khuyenMai.setSoLuong(khuyenMai.getSoLuong() - 1);
            khuyenMai.setSoLuongDaDung(khuyenMai.getSoLuongDaDung() + 1);
            khuyenMaiRepository.saveAndFlush(khuyenMai);
        }

        hoaDonService.add(hoaDon);

        GiaoHang giaoHang = new GiaoHang();
        giaoHang.setHoaDon(hoaDon);
        giaoHangService.saveGiaoHang(giaoHang);
        hoaDon.setGiaoHang(giaoHang);
        hoaDonService.add(hoaDon);

        if (diaChiKHDefault != null) {
            session.removeAttribute("diaChiGiaoHang");
            session.setAttribute("diaChiGiaoHang", diaChiKHDefault);
            hoaDon.setDiaChiNguoiNhan(diaChiKHDefault.getDiaChiChiTiet());
            hoaDon.setSdtNguoiNhan(diaChiKHDefault.getSdtNguoiNhan());
            hoaDon.setTenNguoiNhan(diaChiKHDefault.getTenNguoiNhan());
            giaoHang.setTenNguoiNhan(diaChiKHDefault.getTenNguoiNhan());
            giaoHang.setMaGiaoHang("");
            giaoHang.setSdtNguoiNhan(diaChiKHDefault.getSdtNguoiNhan());
            giaoHang.setDiaChiNguoiNhan(diaChiKHDefault.getDiaChiChiTiet());

            giaoHangService.saveGiaoHang(giaoHang);
            hoaDonService.add(hoaDon);
        }

        HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
        hoaDonChiTiet.setHoaDon(hoaDon);
        hoaDonChiTiet.setChiTietGiay(ctg);
        hoaDonChiTiet.setDonGia(ctg.getGiaBan());
        hoaDonChiTiet.setSoLuong(quantity);
        hoaDonChiTiet.setTgThem(new Date());
        hoaDonChiTiet.setTrangThai(1);

        hoaDonChiTietService.add(hoaDonChiTiet);


        List<HoaDonChiTiet> listHDCTCheckOut = new ArrayList<>();
        listHDCTCheckOut.add(hoaDonChiTiet);
        double tongTienSP = listHDCTCheckOut.stream()
                .mapToDouble(HoaDonChiTiet::getDonGia)
                .sum();
        int sumQuantity = quantity;
        double total = quantity * ctg.getGiaBan();

        List<KhuyenMai> listKM = hoaDonRepository.listDieuKienKhuyenMai(total);
        model.addAttribute("giaTienGiam", giaTienGiam);
        model.addAttribute("dieuKienKhuyenMai", listKM);

        hoaDon.setTongSP(sumQuantity);
        hoaDon.setTongTienSanPham(total);

        hoaDonService.add(hoaDon);
        model.addAttribute("tongTienSP", tongTienSP);
        model.addAttribute("sumQuantity", sumQuantity);
        model.addAttribute("total", total);
        model.addAttribute("listProductCheckOut", listHDCTCheckOut);
        model.addAttribute("toTalOder", total);

        if (diaChiKHDefault != null) {
            Double shippingFee = shippingFeeService.calculatorShippingFee(hoaDon, 25000.0);
            hoaDon.setTongTien(total + shippingFee - giaTienGiam);
            hoaDon.setTienShip(shippingFee);
            hoaDonService.add(hoaDon);

            giaoHang.setDiaChiNguoiNhan(diaChiKHDefault.getDiaChiChiTiet());
            giaoHang.setSdtNguoiNhan(diaChiKHDefault.getSdtNguoiNhan());
            giaoHang.setTenNguoiNhan(diaChiKHDefault.getTenNguoiNhan());
            giaoHangService.saveGiaoHang(giaoHang);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, shippingFeeService.tinhNgayNhanDuKien(diaChiKHDefault.getDiaChiChiTiet()));
            Date ngayDuKien = calendar.getTime();
            model.addAttribute("ngayDuKien", ngayDuKien);
            model.addAttribute("shippingFee", shippingFee);
            model.addAttribute("billPlaceOrder", hoaDon);
            model.addAttribute("toTalOder", total + shippingFee - giaTienGiam);
            model.addAttribute("tongTienDaGiamVoucherShip", total + shippingFee);
            model.addAttribute("diaChiKHDefault", diaChiKHDefault);
            model.addAttribute("addNewAddressNotNull", true);
            model.addAttribute("listAddressKH", diaChiKHList);
        } else {
            model.addAttribute("tongTienDaGiamVoucherShip", total);
            model.addAttribute("addNewAddressNulll", true);
            model.addAttribute("addNewAddressNull", true);
        }

        session.removeAttribute("hoaDonTaoMoi");

        session.setAttribute("hoaDonTaoMoi", hoaDon);

        showData(model);

        return "online/checkout";
    }

    @GetMapping("/vnpay-payment")
    public String GetMapping(Model model) throws ParseException {
        int paymentStatus = vnPayService.orderReturn(request);
        HoaDon hoaDon = (HoaDon) session.getAttribute("hoaDonTaoMoi");

        if (hoaDon == null) {
            hoaDon = (HoaDon) session.getAttribute("HoaDonThanhToanNhanNgu");
        }

        String paymentTime = request.getParameter("vnp_PayDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = sdf.parse(paymentTime);
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");

        if (paymentStatus == 1) {

            LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
            lichSuThanhToan.setTgThanhToan(date);
            lichSuThanhToan.setSoTienThanhToan(hoaDon.getTongTien());
            lichSuThanhToan.setNoiDungThanhToan("Đã thanh toán thành công hóa đơn " + hoaDon.getMaHD() + " ------   ||  Mã VNPAY : " + transactionId + " ||  Số tiền : " + totalPrice);
            lichSuThanhToan.setKhachHang(khachHang);
            lichSuThanhToan.setHoaDon(hoaDon);
            lichSuThanhToan.setMaLSTT("LSTT" + khachHang.getMaKH() + generateRandomNumbers());
            lichSuThanhToan.setTrangThai(0);
            lsThanhToanService.addLSTT(lichSuThanhToan);

            GiaoHang giaoHang = hoaDon.getGiaoHang();
            giaoHang.setTgThanhToan(date);
            giaoHangService.saveGiaoHang(giaoHang);

            hoaDon.setTrangThai(1);
            hoaDonService.add(hoaDon);

            GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
            List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
            List<HoaDon> listHoaDonByKhachHang = hoaDonService.findHoaDonByKhachHang(khachHang);
            List<HoaDon> listHoaDonChoThanhToan = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 0);
            Integer sumProductInCart = listGHCTActive.size();

            model.addAttribute("fullNameLogin", khachHang.getHoTenKH());
            model.addAttribute("sumProductInCart", sumProductInCart);
            model.addAttribute("pagePurchaseUser", true);
            model.addAttribute("purchaseAll", true);
            model.addAttribute("modalVNPaySuccess", true);
            model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);
            model.addAttribute("listHoaDonChoThanhToan", listHoaDonChoThanhToan);
            model.addAttribute("type1", "active");

            return "online/user";
        } else {
            hoaDon.setTrangThai(0);
            hoaDonService.add(hoaDon);

            GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
            List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
            List<HoaDon> listHoaDonByKhachHang = hoaDonService.findHoaDonByKhachHang(khachHang);
            List<HoaDon> listHoaDonChoThanhToan = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 0);
            Integer sumProductInCart = listGHCTActive.size();

            model.addAttribute("fullNameLogin", khachHang.getHoTenKH());
            model.addAttribute("sumProductInCart", sumProductInCart);
            model.addAttribute("pagePurchaseUser", true);
            model.addAttribute("purchaseAll", true);
            model.addAttribute("modalVNPayError", true);
            model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);
            model.addAttribute("listHoaDonChoThanhToan", listHoaDonChoThanhToan);
            model.addAttribute("type1", "active");

            return "online/user";
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

    private void showData(Model model) {
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        model.addAttribute("sumProductInCart", ghctService.findByGHActive(gioHang).size());

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        model.addAttribute("fullNameLogin", khachHang.getHoTenKH());
    }

    @PostMapping("/buyer/checkout/chon-khuyen-mai/{idKM}")
    public String chonKM(Model model, @PathVariable("idKM") UUID idKM, HttpSession session, RedirectAttributes redirectAttributes) {
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(idKM).orElse(null);
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");

        if (khuyenMai != null) {
            GioHang gioHang = (GioHang) session.getAttribute("GHLogged");

            Date date = new Date();
            HoaDon hoaDon = new HoaDon();

            String maHD = "HD_" + khachHang.getMaKH() + "_" + date.getDate() + generateRandomNumbers();

            hoaDon.setKhachHang(khachHang);
            hoaDon.setMaHD(maHD);
            hoaDon.setLoaiHD(0);
            hoaDon.setTgTao(date);
            hoaDon.setTrangThai(6);
            hoaDonService.add(hoaDon);

            hoaDon.setKhuyenMai(khuyenMai);
            hoaDonService.add(hoaDon);

        }

        return "redirect:/buyer/checkout?" + session.getAttribute("checkoutParams" + khachHang.getIdKH()).toString() + "&idKM=" + idKM;
    }
}
