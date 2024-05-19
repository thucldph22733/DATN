package com.example.shoesmanagement.buyerController;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private ThongBaoServices thongBaoServices;


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

        UserForm(model, khachHang);
        return "online/user";
    }

    @GetMapping("/addresses")
    private String getAddressAccount(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");

        UserForm(model, khachHang);

        List<DiaChiKH> diaChiKHDefaultList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, true, 1);
        List<DiaChiKH> diaChiKHList = diaChiKHService.findbyKhachHangAndLoaiAndTrangThai(khachHang, false, 1);

        if (diaChiKHDefaultList.size() == 0) {
            model.addAttribute("diaChiShowNull", true);
        } else {
            model.addAttribute("diaChiShow", true);
            model.addAttribute("addressKHDefault", diaChiKHDefaultList.get(0));
            model.addAttribute("listCartDetail", diaChiKHList);

        }
        model.addAttribute("pageAddressesUser", true);
        model.addAttribute("editAddresses", true);
        model.addAttribute("addNewAddressSetting", true);
        showThongBao(model, khachHang);
        return "online/user";
    }

    @PostMapping("/addresses/add")
    private String addnewAddress(Model model, @RequestParam(name = "defaultSelected", defaultValue = "false") boolean defaultSelected) {
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");

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

    @GetMapping("/notification")
    private String getNotidicationAccount(Model model) {

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");

        UserForm(model, khachHang);

        model.addAttribute("pageNotificationUser", true);
        return "online/user";
    }

    private void UserForm(Model model, KhachHang khachHang) {
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");
        model.addAttribute("fullNameLogin", khachHang.getHoTenKH());

        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();
        model.addAttribute("sumProductInCart", sumProductInCart);
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

    private void showThongBao(Model model, KhachHang khachHang) {
        int soThongBao = 0;

        List<ThongBaoKhachHang> thongBaoKhachHangs = thongBaoServices.findByKhachHang(khachHang);
        for (ThongBaoKhachHang x : thongBaoKhachHangs) {
            if (x.getTrangThai() == 1) {
                soThongBao++;
            }
        }

        model.addAttribute("soThongBao", soThongBao);
        model.addAttribute("listThongBao", thongBaoKhachHangs);
    }

}

