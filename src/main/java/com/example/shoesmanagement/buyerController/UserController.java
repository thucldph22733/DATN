
package com.example.shoesmanagement.buyerController;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.repository.KhuyenMaiRepository;
import com.example.shoesmanagement.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/buyer")
public class UserController {

    @Autowired
    private HttpSession session;

    @Autowired
    private GHCTService ghctService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private DiaChiKHService diaChiKHService;

    @Autowired
    private HoaDonChiTIetService hoaDonChiTietService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private ShippingFeeService shippingFeeService;

    @Autowired
    private GiaoHangService giaoHangService;

    @Autowired
    private LSThanhToanService lsThanhToanService;

    @Autowired
    private GiayChiTietService giayChiTietService;

    @Autowired
    private ViTriDonHangService viTriDonHangServices;

    @Autowired
    private VNPayService vnPayService;

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    @ModelAttribute("dsTrangThai")
    public Map<Integer, String> getDsTrangThai() {
        Map<Integer, String> dsTrangThai = new HashMap<>();
        dsTrangThai.put(1, "Hoạt động");
        dsTrangThai.put(0, "Không hoạt động");
        return dsTrangThai;
    }

    @ModelAttribute("dsLoaiDC")
    public Map<Boolean, String> getDsLoaiDC() {
        Map<Boolean, String> dsLoaiDC = new HashMap<>();
        dsLoaiDC.put(true, "Mặc định");
        dsLoaiDC.put(false, "Không mặc định");

        return dsLoaiDC;
    }

    @GetMapping("/setting")
    private String getSettingAccount(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
        model.addAttribute("khachHang1", khachHang1);
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UserForm(model, khachHang);

        model.addAttribute("pagesettingAccount", true);
        return "online/user";
    }

    @PostMapping("/update/user")
    public String updateUser(HttpServletRequest request,Model model) {
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        }
        String hoTenKH = request.getParameter("hoTenKH");
        String gioiTinh = request.getParameter("gioiTinh");
        String ngaySinh = request.getParameter("ngaySinh");
        String emailKH = request.getParameter("emailKH");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date dateOfBirth = null;
        try {
            dateOfBirth = formatter.parse(ngaySinh);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
        khachHang1.setHoTenKH(hoTenKH);
        khachHang1.setGioiTinh(Integer.parseInt(gioiTinh));
        khachHang1.setNgaySinh(dateOfBirth);
        khachHang1.setEmailKH(emailKH);
        khachHangService.save(khachHang1);
        return "redirect:/buyer/setting";
    }


    @GetMapping("/addresses")
    public String getAddressAccount(Model model) {
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        }

        List<DiaChiKH> diaChiKHDefaultList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, true, 1);
        List<DiaChiKH> diaChiKHList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, false, 1);

        if (diaChiKHDefaultList.isEmpty()) {
            model.addAttribute("diaChiShowNull", true);
            model.addAttribute("diaChiShow", false);
        } else {
            model.addAttribute("diaChiShow", true);
            model.addAttribute("addressKHDefault", diaChiKHDefaultList.get(0));
            model.addAttribute("listCartDetail", diaChiKHList);
            model.addAttribute("diaChiShowNull", false);
        }
        model.addAttribute("pageAddressesUser", true);
        model.addAttribute("editAddresses", true);
        model.addAttribute("addNewAddressSetting", true);

        return "online/user";
    }


    @PostMapping("/addresses/add")
    private String addNewAddress(Model model, @RequestParam(name = "defaultSelected", defaultValue = "false") boolean defaultSelected) {
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
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
        diaChiKH.setMaDC("DC_" + khachHang.getMaKH() + generateRandomNumbers());
        diaChiKH.setSdtNguoiNhan(phoneAddress);
        diaChiKH.setQuanHuyen(district);
        diaChiKH.setTenDC(nameAddress);
        diaChiKH.setTinhTP(city);
        diaChiKH.setTenNguoiNhan(fullName);
        diaChiKH.setXaPhuong(ward);
        diaChiKH.setTgThem(new Date());

        // Kiểm tra nếu người dùng đã có địa chỉ mặc định
        List<DiaChiKH> diaChiKHDefaultList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, true, 1);
        if (!diaChiKHDefaultList.isEmpty()) {
            // Cập nhật địa chỉ mặc định cũ thành không mặc định
            DiaChiKH oldDefaultAddress = diaChiKHDefaultList.get(0);
            oldDefaultAddress.setLoai(false);
            diaChiKHService.save(oldDefaultAddress);
        }

        // Đặt địa chỉ mới làm mặc định nếu cần
        diaChiKH.setLoai(defaultSelected);

        diaChiKHService.save(diaChiKH);

        return "redirect:/buyer/addresses";
    }


    @GetMapping("/addresses/viewEdit/{id}")
    public String viewEditAddresses(@PathVariable UUID id, Model model, @ModelAttribute("userInput") DiaChiKH userInputDC) {
        DiaChiKH diaChiKH = diaChiKHService.getByIdDiaChikh(id);
        model.addAttribute("diaChi", diaChiKH);
        List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
        Collections.sort(khachHangs, (a, b) -> b.getTgThem().compareTo(a.getTgThem()));
        model.addAttribute("khachHang", khachHangs);
        model.addAttribute("diaChiUpdate", userInputDC);
        return "online/edit-address";
    }

    @PostMapping("/addresses/viewEdit/{id}")
    public String editAddresses(@PathVariable UUID id, @Valid @ModelAttribute("diaChi") DiaChiKH diaChiKH
            , RedirectAttributes redirectAttributes) {

        DiaChiKH diaChiKHdb = diaChiKHService.getByIdDiaChikh(id);

        if (diaChiKHdb != null) {
            diaChiKHdb.setTenDC(diaChiKH.getTenDC());
            diaChiKHdb.setTenNguoiNhan(diaChiKH.getTenNguoiNhan());
            diaChiKHdb.setSdtNguoiNhan(diaChiKH.getSdtNguoiNhan());
            diaChiKHdb.setXaPhuong(diaChiKH.getXaPhuong());
            diaChiKHdb.setQuanHuyen(diaChiKH.getQuanHuyen());
            diaChiKHdb.setTinhTP(diaChiKH.getTinhTP());
            diaChiKHdb.setMoTa(diaChiKH.getMoTa());
            diaChiKHdb.setTgSua(new Date());
            diaChiKHService.save(diaChiKHdb);
            redirectAttributes.addFlashAttribute("message", true);
        }
        return "redirect:/buyer/addresses";
    }


    @GetMapping("/addresses/delete/{idDC}")
    private String deleteAddress(Model model, @PathVariable UUID idDC) {

        DiaChiKH diaChiKH = diaChiKHService.getByIdDiaChikh(idDC);
        diaChiKH.setTrangThai(0);
        diaChiKHService.save(diaChiKH);

        return "redirect:/buyer/addresses";
    }

    @GetMapping("/addresses/setDefault/{idDC}")
    private String setDefaultAddress(Model model, @PathVariable UUID idDC) {
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");

        DiaChiKH diaChiKH = diaChiKHService.getByIdDiaChikh(idDC);
        List<DiaChiKH> diaChiKHDefaultList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, true, 1);
        for (DiaChiKH x : diaChiKHDefaultList) {
            x.setLoai(false);
            diaChiKHService.save(x);
        }
        diaChiKH.setLoai(true);
        diaChiKHService.save(diaChiKH);

        return "redirect:/buyer/addresses";
    }

    @GetMapping("/purchase")
    private String getPurchaseAccount(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
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

        return "online/user";

    }

    @GetMapping("/purchase/pay")
    private String getPurchasePay(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UserForm(model, khachHang);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        List<HoaDon> listHoaDonByKhachHang = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 0);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("pagePurchaseUser", true);
        model.addAttribute("purchasePay", true);
        model.addAttribute("type2", "active");
        return "online/user";
    }

    @GetMapping("/purchase/ship")
    private String getPurchaseShip(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UserForm(model, khachHang);

        List<HoaDon> listHoaDonByKhachHang = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 1);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);
        model.addAttribute("pagePurchaseUser", true);
        model.addAttribute("purchaseShip", true);
        model.addAttribute("type3", "active");

        return "online/user";
    }

    @GetMapping("/purchase/confirm")
    private String getPurchaseShipConfirm(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UserForm(model, khachHang);

        List<HoaDon> listHoaDonByKhachHang = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 2);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);
        model.addAttribute("pagePurchaseUser", true);
        model.addAttribute("purchaseShipConfirm", true);
        model.addAttribute("type4", "active");

        return "online/user";
    }

    @GetMapping("/purchase/receive")
    private String getPurchaseReceive(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UserForm(model, khachHang);

        List<HoaDon> listHoaDonByKhachHang = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 3);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);

        model.addAttribute("pagePurchaseUser", true);
        model.addAttribute("purchaseReceive", true);
        model.addAttribute("type5", "active");
        return "online/user";
    }

    @GetMapping("/purchase/completed")
    private String getPurchaseCompleted(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UserForm(model, khachHang);

        List<HoaDon> listHoaDonByKhachHang = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 4);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);

        model.addAttribute("pagePurchaseUser", true);
        model.addAttribute("purchaseCompleted", true);
        model.addAttribute("type6", "active");
        return "online/user";
    }

    @GetMapping("/purchase/cancel")
    private String getPurchaseCancel(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UserForm(model, khachHang);

        List<HoaDon> listHoaDonByKhachHang = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 5);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);

        model.addAttribute("pagePurchaseUser", true);
        model.addAttribute("purchaseCancel", true);
        model.addAttribute("type7", "active");
        return "online/user";
    }

    @GetMapping("/purchase/refund")
    private String getPurchaseRefund(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UserForm(model, khachHang);

        List<HoaDon> listHoaDonByKhachHang = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 3);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);

        model.addAttribute("pagePurchaseUser", true);
        model.addAttribute("purchaseRefund", true);
        model.addAttribute("type8", "active");
        return "online/user";
    }

    @GetMapping("/purchase/bill/detail/{idHD}")
    private String getDetailForm(Model model, @PathVariable UUID idHD) {
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (khachHang == null) return "redirect:/buyer/login";

        List<DiaChiKH> diaChiKHList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, false, 1);
        DiaChiKH diaChiKHDefault = diaChiKHService.findDCKHDefaulByKhachHang(khachHang);

        List<KhuyenMai> khuyenMai = khuyenMaiService.getAllKhuyenMai();
        model.addAttribute("khuyenMai", khuyenMai);

        UserForm(model, khachHang);

        HoaDon hoaDon = hoaDonService.getOne(idHD);

        Double giaTienGiam = null;
        if (hoaDon.getKhuyenMai() != null && hoaDon.getKhuyenMai().getGiaTienGiam() != null) {
            giaTienGiam = hoaDon.getKhuyenMai().getGiaTienGiam();
        } else {
            giaTienGiam = 0.0;
        }
        model.addAttribute("giaTienGiam", giaTienGiam);


        int trangThai = hoaDon.getTrangThai();
        if (trangThai == 0) {

            Date ngayBatDau = hoaDon.getTgTao();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(ngayBatDau);

            // Thực hiện cộng thêm 30 ngày
            calendar.add(Calendar.DATE, 2);

            // Lấy ngày kết thúc
            Date ngayKetThuc = calendar.getTime();

            model.addAttribute("ngayKetThuc", ngayKetThuc);
            GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
            List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
            Integer sumProductInCart = listGHCTActive.size();
            model.addAttribute("sumProductInCart", sumProductInCart);

            model.addAttribute("detailBillPay", true);
            model.addAttribute("modalThayDoiPhuongThucThanhToan", true);
            model.addAttribute("billDetailPay", hoaDon);
            session.removeAttribute("hoaDonPayDetail");
            session.setAttribute("hoaDonPayDetail", hoaDon);

        } else if (trangThai == 1) {

            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            model.addAttribute("giaoHangListActive", giaoHangListActive);
            model.addAttribute("modalThayDoiPhuongThucThanhToan", true);

            model.addAttribute("detailBillShip", true);
            model.addAttribute("billDetailShip", hoaDon);
            List<ViTriDonHang> viTriDonHangList = viTriDonHangServices.findByGiaoHang(giaoHangListActive);
            model.addAttribute("listViTriDonHang", viTriDonHangList);

            session.removeAttribute("hoaDonPayDetail");
            session.setAttribute("hoaDonPayDetail", hoaDon);

        } else if (trangThai == 2) {

            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            model.addAttribute("giaoHangListActive", giaoHangListActive);
            model.addAttribute("modalThayDoiPhuongThucThanhToan", true);

            model.addAttribute("detailBillShipConfirm", true);
            model.addAttribute("billShipConfirm", hoaDon);
            List<ViTriDonHang> viTriDonHangList = viTriDonHangServices.findByGiaoHang(giaoHangListActive);
            model.addAttribute("listViTriDonHang", viTriDonHangList);

            session.removeAttribute("hoaDonPayDetail");
            session.setAttribute("hoaDonPayDetail", hoaDon);

        } else if (trangThai == 3) {

            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            List<ViTriDonHang> viTriDonHangList = viTriDonHangServices.findByGiaoHang(giaoHangListActive);

            model.addAttribute("listViTriDonHang", viTriDonHangList);
            model.addAttribute("detailBillRecieve", true);
            model.addAttribute("billDetailRecieve", hoaDon);
        } else if (trangThai == 4) {
            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();

            Calendar calendar = Calendar.getInstance();

            calendar.add(Calendar.DATE, 2);

            Date ngayKetThuc = calendar.getTime();
            model.addAttribute("ngayKetThucHoanHang", ngayKetThuc);

            List<ViTriDonHang> viTriDonHangList = viTriDonHangServices.findByGiaoHang(giaoHangListActive);
            model.addAttribute("listViTriDonHang", viTriDonHangList);

            model.addAttribute("detailBillCompleted", true);
            model.addAttribute("billDetailCompleted", hoaDon);
        } else if (trangThai == 5) {
            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();

            List<ViTriDonHang> viTriDonHangList = viTriDonHangServices.findByGiaoHang(giaoHangListActive);
            model.addAttribute("listViTriDonHang", viTriDonHangList);
            model.addAttribute("detailBillCancel", true);
            model.addAttribute("billDetailCancel", hoaDon);
        } else if (trangThai == 6) {
            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            model.addAttribute("giaoHangListActive", giaoHangListActive);

            model.addAttribute("detailBillRefund", true);
            model.addAttribute("billDetailRefund", hoaDon);
        }

        model.addAttribute("listAddressKH", diaChiKHList);
        model.addAttribute("diaChiKHDefault", diaChiKHDefault);
        return "online/user";
    }


    @GetMapping("/purchase/bill/detail/cancel/{idHD}")
    private String getDetailCancelForm(Model model, @PathVariable UUID idHD) {
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        List<DiaChiKH> diaChiKHList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, false, 1);
        DiaChiKH diaChiKHDefault = diaChiKHService.findDCKHDefaulByKhachHang(khachHang);

        UserForm(model, khachHang);

        HoaDon hoaDon = hoaDonService.getOne(idHD);

        int trangThai = hoaDon.getTrangThai();
        if (trangThai == 0) {
            model.addAttribute("modalHuyHoaDonInDetailBillPay", true);
            model.addAttribute("modalThayDoiPhuongThucThanhToan", true);

            model.addAttribute("detailBillPay", true);
            model.addAttribute("billDetailPay", hoaDon);

            session.removeAttribute("hoaDonPayDetail");
            session.setAttribute("hoaDonPayDetail", hoaDon);

        } else if (trangThai == 1) {
            model.addAttribute("modalHuyHoaDonInDetailBillPay", true);

            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            model.addAttribute("giaoHangListActive", giaoHangListActive);
            model.addAttribute("modalThayDoiPhuongThucThanhToan", true);

            model.addAttribute("detailBillShip", true);
            model.addAttribute("billDetailShip", hoaDon);

            session.removeAttribute("hoaDonPayDetail");
            session.setAttribute("hoaDonPayDetail", hoaDon);

        } else if (trangThai == 2) {
            model.addAttribute("modalHuyHoaDonInDetailBillPayConfirm", true);

            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            model.addAttribute("giaoHangListActive", giaoHangListActive);
            model.addAttribute("modalThayDoiPhuongThucThanhToan", true);

            model.addAttribute("detailBillShipConfirm", true);
            model.addAttribute("billShipConfirm", hoaDon);

            session.removeAttribute("hoaDonPayDetail");
            session.setAttribute("hoaDonPayDetail", hoaDon);

        } else if (trangThai == 3) {

            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            model.addAttribute("giaoHangListActive", giaoHangListActive);

            model.addAttribute("detailBillRecieve", true);
            model.addAttribute("billDetailRecieve", hoaDon);

        } else if (trangThai == 4) {
            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            model.addAttribute("giaoHangListActive", giaoHangListActive);

            model.addAttribute("detailBillCompleted", true);
            model.addAttribute("billDetailCompleted", hoaDon);
        } else if (trangThai == 5) {
            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            model.addAttribute("giaoHangListActive", giaoHangListActive);

            model.addAttribute("detailBillCancel", true);
            model.addAttribute("billDetailCancel", hoaDon);

        } else if (trangThai == 6) {
            GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
            model.addAttribute("giaoHangListActive", giaoHangListActive);

            model.addAttribute("detailBillRefund", true);
            model.addAttribute("billDetailRefund", hoaDon);

        }

        model.addAttribute("listAddressKH", diaChiKHList);
        model.addAttribute("diaChiKHDefault", diaChiKHDefault);

        return "online/user";
    }

    @PostMapping("/purchase/pay/change/payment/{idHD}")
    private String changePaymentMethod(Model model, @PathVariable UUID idHD) {
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        HoaDon hoaDon = (HoaDon) session.getAttribute("hoaDonPayDetail");
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        String hinhThucThayDoi = request.getParameter("paymentMethod");

        if (hinhThucThayDoi.equals("qrCodeBanking")) {
            UserForm(model, khachHang);
            hoaDon.setHinhThucThanhToan(1);
            hoaDon.setTrangThai(0);
            hoaDonService.add(hoaDon);

            LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
            lichSuThanhToan.setTgThanhToan(new Date());
            lichSuThanhToan.setSoTienThanhToan(hoaDon.getTongTien());
            lichSuThanhToan.setNoiDungThanhToan(hoaDon.getMaHD());
            lichSuThanhToan.setKhachHang(khachHang);
            lichSuThanhToan.setHoaDon(hoaDon);
            lichSuThanhToan.setMaLSTT("LSTT" + khachHang.getMaKH() + generateRandomNumbers());
            lichSuThanhToan.setTrangThai(0);
            lichSuThanhToan.setLoaiTT(0);
            lichSuThanhToan.setNoiDungThanhToan("Thay đổi hình thức thanh toán sang VNPAY");
            lsThanhToanService.addLSTT(lichSuThanhToan);

            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            double doubleNumber = hoaDon.getTongTien();
            int total = (int) doubleNumber;

            String vnpayUrl = vnPayService.createOrder(total, "orderInfo", baseUrl);
            hoaDon.setHinhThucThanhToan(1);
            hoaDon.setTrangThai(0);
            hoaDonService.add(hoaDon);

            session.removeAttribute("HoaDonThanhToan");
            session.setAttribute("HoaDonThanhToan", hoaDon);

            return "redirect:" + vnpayUrl;

        } else {
            UserForm(model, khachHang);

            hoaDon.setHinhThucThanhToan(0);
            hoaDon.setTrangThai(1);
            hoaDonService.add(hoaDon);

            return "redirect:/buyer/purchase/ship";
        }

    }

    @PostMapping("/purchase/bill/pay/cancel/{idHD}")
    private String cancelBillPay(Model model, @PathVariable UUID idHD, HttpServletRequest request, HttpSession session) {
        String lyDoHuy = request.getParameter("lyDoHuy");
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
            // Nếu không có khách hàng đăng nhập, quay lại trang đăng nhập
            return "redirect:/buyer/login";
        }

        HoaDon hoaDonHuy = hoaDonService.getOne(idHD);
        LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
        lichSuThanhToan.setTgThanhToan(new Date());
        lichSuThanhToan.setSoTienThanhToan(hoaDonHuy.getTongTien());
        lichSuThanhToan.setNoiDungThanhToan(hoaDonHuy.getMaHD());
        lichSuThanhToan.setKhachHang(khachHang);
        lichSuThanhToan.setHoaDon(hoaDonHuy);
        lichSuThanhToan.setMaLSTT("LSTT" + khachHang.getMaKH() + generateRandomNumbers());
        lichSuThanhToan.setLoaiTT(0);

        GiaoHang giaoHang = hoaDonHuy.getGiaoHang();
        giaoHang.setTgHuy(new Date());

        if ("changeProduct".equals(lyDoHuy)) {
            lyDoHuy = "Tôi muốn thay đổi sản phẩm";
        } else if ("none".equals(lyDoHuy)) {
            lyDoHuy = "Tôi không có nhu cầu mua nữa";
        } else if ("lyDoKhac".equals(lyDoHuy)) {
            String customReason = request.getParameter("customReason");
            lyDoHuy = (customReason != null && !customReason.trim().isEmpty()) ? customReason : "Lý do khác";
        } else if ("changeSize".equals(lyDoHuy)) {
            lyDoHuy = "Tôi muốn thay đổi size/màu";
        }

        giaoHang.setLyDoHuy(lyDoHuy);
        giaoHangService.saveGiaoHang(giaoHang);

        if (hoaDonHuy.getHinhThucThanhToan() == 1) {
            if (hoaDonHuy.getTrangThai() == 1) {
                lichSuThanhToan.setTrangThai(3);
                lichSuThanhToan.setNoiDungThanhToan("Hủy đơn hàng đã thanh toán");
            } else if (hoaDonHuy.getTrangThai() == 0) {
                lichSuThanhToan.setTrangThai(2);
                lichSuThanhToan.setNoiDungThanhToan("Hủy đơn hàng chưa thanh toán");
            }
            lsThanhToanService.addLSTT(lichSuThanhToan);
        }

        hoaDonHuy.setTrangThai(5);
        hoaDonHuy.setLyDoHuy(lyDoHuy);
        hoaDonHuy.setTgHuy(new Date());
        hoaDonService.add(hoaDonHuy);

        if ("changeSize".equals(lyDoHuy)) {
            List<HoaDonChiTiet> listHDCT = hoaDonHuy.getHoaDonChiTiets();
            for (HoaDonChiTiet hdct : listHDCT) {
                ChiTietGiay chiTietGiay = hdct.getChiTietGiay();
                chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() + hdct.getSoLuong());
                if (chiTietGiay.getSoLuong() > 0) {
                    chiTietGiay.setTrangThai(1);
                }
                giayChiTietService.save(chiTietGiay);
            }
        }

        return "redirect:/buyer/purchase/cancel";
    }

    @GetMapping("/purchase/bill/pay/{idHD}")
    private String payBill(Model model, @PathVariable UUID idHD) {
        HoaDon hoaDon = hoaDonService.getOne(idHD);
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        double doubleNumber = hoaDon.getTongTien();
        int total = (int) doubleNumber;

        String vnpayUrl = vnPayService.createOrder(total, "orderInfo", baseUrl);
        hoaDon.setHinhThucThanhToan(1);
        hoaDon.setTrangThai(0);
        hoaDonService.add(hoaDon);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        session.removeAttribute("HoaDonThanhToan");
        session.setAttribute("HoaDonThanhToan", hoaDon);

        return "redirect:" + vnpayUrl;

    }

    @GetMapping("/purchaser/bill/buy/again/{idHD}")
    private String buyAgain(Model model, @PathVariable UUID idHD) {
        HoaDon hoaDonBuyAgain = hoaDonService.getOne(idHD);

        List<HoaDonChiTiet> hoaDonChiTietList = hoaDonChiTietService.findByHoaDon(hoaDonBuyAgain);

        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");

        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        for (HoaDonChiTiet x : hoaDonChiTietList) {

            GioHangChiTiet gioHangChiTietExist = ghctService.findByCTSPActiveAndTrangThai(x.getChiTietGiay(), 1);

            if (gioHangChiTietExist != null) {

                gioHangChiTietExist.setSoLuong(gioHangChiTietExist.getSoLuong() + x.getSoLuong());
                gioHangChiTietExist.setTgThem(new Date());
                gioHangChiTietExist.setDonGia(x.getSoLuong() * x.getChiTietGiay().getGiaBan() + gioHangChiTietExist.getDonGia());
                ghctService.addNewGHCT(gioHangChiTietExist);

            } else {

                GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();

                gioHangChiTiet.setGioHang(gioHang);
                gioHangChiTiet.setDonGia(x.getSoLuong() * x.getChiTietGiay().getGiaBan());
                gioHangChiTiet.setChiTietGiay(x.getChiTietGiay());
                gioHangChiTiet.setSoLuong(x.getSoLuong());
                gioHangChiTiet.setTrangThai(1);
                gioHangChiTiet.setTgThem(new Date());

                ghctService.addNewGHCT(gioHangChiTiet);
            }
        }

        return "redirect:/buyer/cart";
    }

    @PostMapping("/purchase/bill/add/address/get/{idHD}")
    private String addAddressNhanHang(Model model,
                                      @PathVariable UUID idHD,
                                      @RequestParam(name = "defaultSelected", defaultValue = "false") boolean defaultSelected) {

        HoaDon hoaDon = hoaDonService.getOne(idHD);
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
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
        diaChiKH.setMaDC("DC_" + khachHang.getMaKH() + generateRandomNumbers());
        diaChiKH.setSdtNguoiNhan(phoneAddress);
        diaChiKH.setQuanHuyen(district);
        diaChiKH.setTenDC(nameAddress);
        diaChiKH.setTinhTP(city);
        diaChiKH.setTenNguoiNhan(fullName);
        diaChiKH.setXaPhuong(ward);
        diaChiKH.setTgThem(new Date());
        diaChiKH.setLoai(defaultSelected);

        diaChiKHService.save(diaChiKH);

//        hoaDon.setTenNguoiNhan(diaChiKH.getTenNguoiNhan());
//        hoaDon.setSdtNguoiNhan(diaChiKH.getSdtNguoiNhan());
//        hoaDon.setDiaChiNguoiNhan(diaChiKH.getDiaChiChiTiet());

        hoaDonService.add(hoaDon);

        Double tienShip = shippingFeeService.calculatorShippingFee(hoaDon, 25000.0);

        double tienShipCu = hoaDon.getTienShip();

        Date ngayBatDau = hoaDon.getTgTao();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ngayBatDau);

        calendar.add(Calendar.DATE, shippingFeeService.tinhNgayNhanDuKien(diaChiKH.getDiaChiChiTiet()));

        hoaDon.setTongTien(hoaDon.getTongTien() - tienShipCu + tienShip);
        hoaDon.setTienShip(tienShip);
//        hoaDon.setTgNhanDK(calendar.getTime());
//        hoaDon.setTongTienDG(hoaDon.getTongTienDG() + tienShip - tienShipCu);

        hoaDonService.add(hoaDon);


        UserForm(model, khachHang);

        List<HoaDon> listHoaDonByKhachHang = hoaDonService.findHoaDonByKhachHang(khachHang);

        List<HoaDon> listHoaDonChoThanhToan = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 0);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("pagePurchaseUser", true);
        model.addAttribute("purchaseAll", true);
        model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);
        model.addAttribute("listHoaDonChoThanhToan", listHoaDonChoThanhToan);

        model.addAttribute("type1", "active");

        model.addAttribute("showThongBaoThayDoiDiaChiNhanHangThanhCong", true);

        return "online/user";
    }

    @PostMapping("/purchase/bill/change/address/get/{idHD}")
    private String changeAddressNhanHang(Model model, @PathVariable UUID idHD) {
        HoaDon hoaDon = hoaDonService.getOne(idHD);
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            KhachHang khachHang1 = khachHangService.getByIdKhachHang(khachHang.getIdKH());
            model.addAttribute("khachHang1", khachHang1);
        } else {
        }
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UUID idDCKH = UUID.fromString(request.getParameter("idDCKH"));

        DiaChiKH diaChiKH = diaChiKHService.findByIdDiaChiKH(idDCKH);

//        hoaDon.setTenNguoiNhan(diaChiKH.getTenNguoiNhan());
//        hoaDon.setSdtNguoiNhan(diaChiKH.getSdtNguoiNhan());
//        hoaDon.setDiaChiNguoiNhan(diaChiKH.getDiaChiChiTiet());

        hoaDonService.add(hoaDon);

        Double tienShip = shippingFeeService.calculatorShippingFee(hoaDon, 25000.0);

        double tienShipCu = hoaDon.getTienShip();

        Date ngayBatDau = hoaDon.getTgTao();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ngayBatDau);

        calendar.add(Calendar.DATE, shippingFeeService.tinhNgayNhanDuKien(diaChiKH.getDiaChiChiTiet()));

        hoaDon.setTongTien(hoaDon.getTongTien() - tienShipCu + tienShip);
        hoaDon.setTienShip(tienShip);


        hoaDonService.add(hoaDon);

        UserForm(model, khachHang);
        List<HoaDon> listHoaDonByKhachHang = hoaDonService.findHoaDonByKhachHang(khachHang);
        List<HoaDon> listHoaDonChoThanhToan = hoaDonService.listHoaDonKhachHangAndTrangThaiOnline(khachHang, 0);
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("pagePurchaseUser", true);
        model.addAttribute("purchaseAll", true);
        model.addAttribute("listAllHDByKhachHang", listHoaDonByKhachHang);
        model.addAttribute("listHoaDonChoThanhToan", listHoaDonChoThanhToan);

        model.addAttribute("type1", "active");
        model.addAttribute("showThongBaoThayDoiDiaChiNhanHangThanhCong", true);

        return "online/user";
    }

    @GetMapping("/notification")
    private String getNotidicationAccount(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (session.getAttribute("KhachHangLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/buyer/login";
        }
        UserForm(model, khachHang);

        model.addAttribute("pageNotificationUser", true);
        return "online/user";
    }

    private String UserForm(Model model, KhachHang khachHang) {
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        model.addAttribute("fullNameLogin", khachHang.getHoTenKH());
        if (session.getAttribute("fullNameLogin") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/login";
        }
        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
        return null;
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
}