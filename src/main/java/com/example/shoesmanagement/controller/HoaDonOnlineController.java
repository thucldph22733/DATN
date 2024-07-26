package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.repository.HoaDonChiTietRepository;
import com.example.shoesmanagement.repository.HoaDonRepository;
import com.example.shoesmanagement.repository.KhuyenMaiRepository;
import com.example.shoesmanagement.repository.SizeRepository;
import com.example.shoesmanagement.service.*;
import com.example.shoesmanagement.viewModel.GiayViewModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequestMapping("/manage/bill/")
@Controller
public class HoaDonOnlineController {
    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private GiayService giayService;

    @Autowired
    private GHCTService ghctService;
    @Autowired
    private KhuyenMaiService khuyenMaiService;
    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private NhanVienService nhanVienService;
    @Autowired
    private GiaoHangService giaoHangService;
    private int tongSanPham = 0;

    private double giaBan = 0;

    private double tongTienSanPham = tongSanPham * giaBan;

    private double giaTienGiam = 0;

    private double tongTien = tongTienSanPham - giaTienGiam;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;
    @Autowired
    private GiayViewModelService giayViewModelService;
    @Autowired
    private SizeRepository sizeRepository;
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
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @GetMapping("online")
    private String manageBillOnline(Model model) {
        model.addAttribute("reLoadPage", true);
        List<GiayViewModel> list = giayViewModelService.getAllVm();
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/login";
        }
        if (listG != null && !listG.isEmpty()) {
            System.out.println("Danh sách sản phẩm không rỗng, số lượng: " + listG.size());
            for (GiayViewModel product : listG) {
                System.out.println(product);
            }
        } else {
            System.out.println("Danh sách sản phẩm rỗng hoặc null");
        }
        showData(model);
        showTab1(model);
        model.addAttribute("listSanPham", listG);

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
        model.addAttribute("messageXacNhan", "Xác nhận nhân viên giao thành công.");

        return "manage/manage-bill-online";
    }

    @GetMapping("online/delete/{idHD}")
    public String huyHoaDonOnline(@PathVariable UUID idHD, Model model) {
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/login";
        }
        String lyDoHuy = request.getParameter("lyDoHuy");
        Date date = new Date();
        HoaDon hoaDon = hoaDonService.getOne(idHD);
        hoaDon.setLyDoHuy(lyDoHuy);
        hoaDon.setTgHuy(date);
        hoaDon.setTrangThai(5);

        KhuyenMai khuyenMai = hoaDon.getKhuyenMai();
        khuyenMai.setSoLuong(khuyenMai.getSoLuong() + 1);
        khuyenMai.setSoLuongDaDung(khuyenMai.getSoLuongDaDung() - 1);
        khuyenMaiRepository.saveAndFlush(khuyenMai);

        hoaDonService.save(hoaDon);

        showData(model);
        showTab3(model);
        model.addAttribute("message", "Hóa đơn đã được hủy thành công.");
        return "manage/manage-bill-online";
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

    private void showTab3(Model model) {

        model.addAttribute("activeAll", "nav-link");
        model.addAttribute("xac_nhan_tt", "nav-link");
        model.addAttribute("van_chuyen", "nav-link active");


        model.addAttribute("tabpane1", "tab-pane");
        model.addAttribute("tabpane2", "tab-pane");
        model.addAttribute("tabpane3", "tab-pane show active");
        model.addAttribute("tabpane4", "tab-pane");
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
                    System.out.println("abc");
                } else {
                    tongTienHoaDon += x.getTongTien();
                }
            }
        }

        if (listAllHoaDonOnline != null) {
            for (HoaDon x : listAllHoaDonOnline) {
                if (x.getTrangThai() == 6 || x.getTrangThai() == 7) {
                    System.out.println("abc");
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

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword, Model model,
                         RedirectAttributes redirectAttributes) {
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        if (keyword.length() >= 3 && keyword.substring(0, 3).equals("CTG")) {
            ChiTietGiay chiTietGiay = giayChiTietService.findByMa(keyword);
            if (chiTietGiay == null) {
                redirectAttributes.addFlashAttribute("messageError", true);
                redirectAttributes.addFlashAttribute("tbaoError", "Không tìm thấy sản mã phẩm ");
                return "redirect:/manage/bill/online";
            }
            model.addAttribute("idHoaDon", idHoaDon);
        } else {
            List<GiayViewModel> list = giayViewModelService.getAll(keyword);
            if (list.isEmpty()) {
                redirectAttributes.addFlashAttribute("messageError", true);
                redirectAttributes.addFlashAttribute("tbaoError", "Không tìm thấy sản phẩm");
                return "redirect:/manage/bill/online";
            }
            model.addAttribute("listSanPham", list);
        }

        return "redirect:/manage/bill/online";
    }


    @GetMapping("/xoa-gio-hang2/{idChiTietGiay}")
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
        return "redirect:/manage-bill-online/" + idHoaDon;
    }


//    @PostMapping("/deleteChiTietGiay/{idCTG}/{idHD}")
//    @ResponseBody
//    public ResponseEntity<String> deleteChiTietGiay(@PathVariable UUID idCTG, @PathVariable UUID idHD) {
//        try {
//            // Lấy hóa đơn cần cập nhật
//            HoaDon hoaDon = hoaDonService.getOne(idHD);
//
//            // Lấy chi tiết giày cần xóa
//            ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idCTG);
//
//            // Xóa chi tiết hóa đơn
//            hoaDonChiTietRepository.deleteHoaDonChiTietByChiTietGiay(chiTietGiay.getIdCTG());
//
//            // Cập nhật lại hóa đơn
//            hoaDonService.updateHoaDon(hoaDon);
//
//            // Trả về phản hồi thành công
//            return ResponseEntity.ok("Sản phẩm đã được xoá khỏi giỏ hàng thành công!");
//        } catch (Exception e) {
//            // Trả về phản hồi lỗi
//            return ResponseEntity.status(500).body("Có lỗi xảy ra trong quá trình xoá sản phẩm!");
//        }
//    }


    @GetMapping("/hoadon/{idHoaDon}")
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
            return "redirect:/manage/bill/online";

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
        return "/manage/manage-bill-online";
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

        return "/manage/manage-bill-online";
    }


    @GetMapping("/add-to-hd/{idHD}")
    public String addToCart(@PathVariable("idHD") UUID idHoaDon,
                            @RequestParam("idChiTietGiay") UUID idChiTietGiay,
                            @RequestParam("soLuong") int soLuong, Model model,
                            RedirectAttributes redirectAttributes, HttpSession session) {
        if (idHoaDon == null || idChiTietGiay == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Giá trị idHoaDon hoặc idChiTietGiay bị thiếu");
            return "redirect:/manage/bill/online";
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
            return "redirect:/manage/bill/online" + idHoaDon;
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

        hoaDon.setTongTienSanPham(hoaDon.getTongTienSanPham() + chiTietGiay.getGiaBan() * soLuong);
        hoaDonService.save(hoaDon);

        redirectAttributes.addFlashAttribute("messageSuccess", true);
        redirectAttributes.addFlashAttribute("tb", "Thêm vào giỏ hàng thành công");
        return "redirect:/manage/bill/online" + idHoaDon;
    }




}
