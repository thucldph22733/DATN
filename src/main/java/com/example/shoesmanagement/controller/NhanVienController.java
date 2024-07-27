package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.ChucVu;
import com.example.shoesmanagement.model.Hang;
import com.example.shoesmanagement.model.NhanVien;
import com.example.shoesmanagement.repository.ChucVuRepository;
import com.example.shoesmanagement.repository.NhanVienRepository;
import com.example.shoesmanagement.service.AuthService;
import com.example.shoesmanagement.service.ChucVuService;
import com.example.shoesmanagement.service.NhanVienService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.util.*;

@RequestMapping("/manage")
@Controller
public class NhanVienController {
    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private AuthService authService;

    @Autowired
    private ChucVuService chucVuService;

    @Autowired
    private HttpSession session;

    @Autowired
    private NhanVienRepository nhanVienRepsitory;

    @Autowired
    private ChucVuRepository chucVuRepsitory;

    @ModelAttribute("dsTrangThai")
    public Map<Integer, String> getDsTrangThai() {
        Map<Integer, String> dsTrangThai = new HashMap<>();
        dsTrangThai.put(1, "Đang làm việc");
        dsTrangThai.put(0, "Đã nghỉ việc");
        return dsTrangThai;
    }

    @ModelAttribute("dsGioiTinh")
    public Map<Integer, String> getDsGioiTinh() {
        Map<Integer, String> dsGioiTinh = new HashMap<>();
        dsGioiTinh.put(1, "Nam");
        dsGioiTinh.put(0, "Nữ");
        dsGioiTinh.put(2, "Khác");
        return dsGioiTinh;
    }

    @GetMapping("/nhan-vien")
    public String dsNhanVien(Model model, @ModelAttribute("message") String message) {
        List<NhanVien> nhanViens = nhanVienRepsitory.getAllNhanVien();
        List<ChucVu> chucVus = chucVuService.getAllChucVu();
        model.addAttribute("nhanVien", nhanViens);
        model.addAttribute("chucVu", chucVus);
        if (session.getAttribute("managerLogged") == null) {
            return "redirect:/login";
        }
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        return "manage/nhan-vien";
    }

    @GetMapping("/change-password")

    public String changePasswordPage(Model model) {
        model.addAttribute("email", ""); // Truyền email mặc định vào form

        return "manage/change-pass";
    }

    @PostMapping("/change-pass-post")

    public String changePasswordPost(
            @RequestParam("email") String email,
            @RequestParam("currentPass") String currentPass,
            @RequestParam("newPass") String newPass,
            @RequestParam("reNewPass") String reNewPass,
            RedirectAttributes redirectAttributes) {

        if (!newPass.equals(reNewPass)) {
            redirectAttributes.addFlashAttribute("mess", "Mật khẩu mới và mật khẩu nhập lại không khớp!");
            return "redirect:/manage/change-password";

        }

        NhanVien nhanVien = nhanVienService.findByEmailNV(email);
        if (nhanVien == null) {
            redirectAttributes.addFlashAttribute("mess", "Email không tồn tại!");

            return "redirect:/manage/change-password";

        }

        if (!nhanVien.getMatKhau().equals(currentPass)) {
            redirectAttributes.addFlashAttribute("mess", "Mật khẩu hiện tại không đúng!");

            return "redirect:/manage/change-password";

        }

        nhanVien.setMatKhau(newPass);
        nhanVienService.save(nhanVien);


        redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!!!");
        return "redirect:/login"; // Điều hướng về trang đăng nhập

    }

    @GetMapping("/nhan-vien/viewAdd")
    public String viewAddNhanVien(Model model
            , @ModelAttribute("errorNV") String errorNV
            , @ModelAttribute("userInput") NhanVien userInputNV
            , @ModelAttribute("messageCV") String messageCV
            , @ModelAttribute("maCVError") String maCVError
            , @ModelAttribute("tenCVError") String tenCVError
            , @ModelAttribute("errorCV") String errorCV, @ModelAttribute("userInput") ChucVu userInputCV
            , @ModelAttribute("Errormessage") String Errormessage
            , @ModelAttribute("ErrormessageCV") String ErrormessageCV) {
        List<ChucVu> chucVuList = chucVuService.getAllChucVu();

        Collections.sort(chucVuList, (a, b) -> b.getTgThem().compareTo(a.getTgThem()));
        model.addAttribute("chucVu", chucVuList);
        //
        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("chucVuAdd", new ChucVu());
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        if (userInputNV != null) {
            model.addAttribute("nhanVien", userInputNV);
        }
        //add CV
        if (messageCV == null || !"true".equals(messageCV)) {
            model.addAttribute("messageCV", false);
        }
        if (userInputCV != null) {
            model.addAttribute("chucVuAdd", userInputCV);
        }
        //
        if (ErrormessageCV == null || !"true".equals(ErrormessageCV)) {
            model.addAttribute("ErrormessageCV", false);
        }

        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/add-nhan-vien";
    }

    @PostMapping("/nhan-vien/viewAdd/add")
    public String addNhanVien(@ModelAttribute("nhanVien") NhanVien nhanVien, Model model
            , RedirectAttributes redirectAttributes) {
        List<ChucVu> chucVuList = chucVuService.getAllChucVu();

        Collections.sort(chucVuList, (a, b) -> b.getTgThem().compareTo(a.getTgThem()));
        model.addAttribute("chucVu", chucVuList);
        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("chucVuAdd", new ChucVu());

        NhanVien existingNV = nhanVienRepsitory.findByMaNV(nhanVien.getMaNV());
        if (existingNV != null) {
            redirectAttributes.addFlashAttribute("userInput", nhanVien);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return "redirect:/manage/nhan-vien/viewAdd";
        }
        NhanVien existingNV1 = nhanVienRepsitory.findByEmailNV(nhanVien.getEmailNV());
        if (existingNV1 != null) {
            redirectAttributes.addFlashAttribute("userInput", nhanVien);
            redirectAttributes.addFlashAttribute("ErrormessageeEmail", true);
            return "redirect:/manage/nhan-vien/viewAdd";
        }
        NhanVien existingNV2 = nhanVienRepsitory.findByCCCDNV(nhanVien.getCCCDNV());
        if (existingNV2 != null) {
            redirectAttributes.addFlashAttribute("userInput", nhanVien);
            redirectAttributes.addFlashAttribute("ErrormessageCCCD", true);
            return "redirect:/manage/nhan-vien/viewAdd";
        }
        NhanVien existingNV3 = nhanVienRepsitory.findBySdtNV(nhanVien.getSdtNV());
        if (existingNV3 != null) {
            redirectAttributes.addFlashAttribute("userInput", nhanVien);
            redirectAttributes.addFlashAttribute("ErrormessageSDT", true);
            return "redirect:/manage/nhan-vien/viewAdd";
        }

        NhanVien nhanVien1 = new NhanVien();
        nhanVien1.setMaNV(nhanVien.getMaNV());
        nhanVien1.setHoTenNV(nhanVien.getHoTenNV());
        nhanVien1.setNgaySinh(nhanVien.getNgaySinh());
        nhanVien1.setSdtNV(nhanVien.getSdtNV());
        nhanVien1.setMatKhau(nhanVien.getMatKhau());
        nhanVien1.setEmailNV(nhanVien.getEmailNV());
        nhanVien1.setGioiTinh(nhanVien.getGioiTinh());
        nhanVien1.setCCCDNV(nhanVien.getCCCDNV());
        nhanVien1.setDiaChi(nhanVien.getDiaChi());
        nhanVien1.setAnhNV(nhanVien.getAnhNV());
        nhanVien1.setTrangThai(nhanVien.getTrangThai());
        nhanVien1.setChucVu(nhanVien.getChucVu());
        nhanVien1.setTgThem(new Date());
        nhanVien1.setTrangThai(1);
        nhanVienService.save(nhanVien1);
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/nhan-vien";
    }

    @PostMapping("/nhan-vien/chuc-vu/viewAdd/add")
    public String addChucVu(@Valid @ModelAttribute("chucVuAdd") ChucVu chucVu
            , BindingResult result, RedirectAttributes redirectAttributes) {
        //
        ChucVu existingCV = chucVuRepsitory.findByMaCV(chucVu.getMaCV());
        if (existingCV != null) {
            redirectAttributes.addFlashAttribute("userInput", chucVu);
            redirectAttributes.addFlashAttribute("ErrormessageCV", true);
            return "redirect:/manage/nhan-vien/viewAdd";
        }

        //
        chucVu.setTgThem(new Date());
        chucVu.setTrangThai(1);
        chucVuService.save(chucVu);
        redirectAttributes.addFlashAttribute("messageCV", true);
        return "redirect:/manage/nhan-vien/viewAdd";
    }

    @GetMapping("/nhan-vien/delete/{id}")
    public String deleteNhanVien(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        NhanVien nhanVien = nhanVienService.getByIdNhanVien(id);
        nhanVien.setTrangThai(0);
        nhanVien.setTgSua(new Date());
        nhanVienService.save(nhanVien);
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/nhan-vien";

    }

    public void deleteNVById(UUID idNV) {
        NhanVien nhanVien = nhanVienService.getByIdNhanVien(idNV);
        nhanVien.setTrangThai(0);
        nhanVien.setTgSua(new Date());
        nhanVienService.save(nhanVien);
    }

    @GetMapping("/nhan-vien/viewUpdate/{id}")
    public String viewUpdatenhanVien(@PathVariable UUID id, Model model
            , @ModelAttribute("errorNV") String errorNV, @ModelAttribute("userInput") NhanVien userInputNV
            , @ModelAttribute("messageCV") String messageCV
            , @ModelAttribute("errorCV") String errorCV, @ModelAttribute("userInput") ChucVu userInputCV
            , @ModelAttribute("Errormessage") String Errormessage
            , @ModelAttribute("ErrormessageCV") String ErrormessageCV) {
        NhanVien nhanVien = nhanVienService.getByIdNhanVien(id);
        model.addAttribute("nhanVien", nhanVien);
        //
        List<ChucVu> chucVus = chucVuService.getAllChucVu();
        Collections.sort(chucVus, (a, b) -> b.getTgThem().compareTo(a.getTgThem()));
        model.addAttribute("chucVu", chucVus);
        //
        model.addAttribute("chucVuAdd", new ChucVu());
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        if (userInputNV != null) {
            model.addAttribute("nhanVienUpdate", userInputNV);
        }
        session.setAttribute("id", id);
        //
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }

        return "manage/update-nhan-vien";
    }

    @PostMapping("/nhan-vien/viewUpdate/{id}")
    public String updatenhanVien(@PathVariable UUID id
            , @Valid @ModelAttribute("nhanVien") NhanVien nhanVien
            , RedirectAttributes redirectAttributes) {
        NhanVien nhanViendb = nhanVienService.getByIdNhanVien(id);
        UUID idNV = (UUID) session.getAttribute("id");
        String link = "redirect:/manage/nhan-vien/viewUpdate/" + idNV;
//
        NhanVien existingNV = nhanVienRepsitory.findByMaNV(nhanVien.getMaNV());
        if (existingNV != null && !existingNV.getIdNV().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", nhanVien);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return link;
        }
        //
        NhanVien existingNV1 = nhanVienRepsitory.findByEmailNV(nhanVien.getEmailNV());
        if (existingNV1 != null && !existingNV.getIdNV().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", nhanVien);
            redirectAttributes.addFlashAttribute("ErrormessageeEmail", true);
            return link;
        }
        //
        NhanVien existingNV2 = nhanVienRepsitory.findByCCCDNV(nhanVien.getCCCDNV());
        if (existingNV2 != null && !existingNV.getIdNV().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", nhanVien);
            redirectAttributes.addFlashAttribute("ErrormessageCCCD", true);
            return link;
        }
        NhanVien existingNV3 = nhanVienRepsitory.findBySdtNV(nhanVien.getSdtNV());
        if (existingNV3 != null && !existingNV.getIdNV().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", nhanVien);
            redirectAttributes.addFlashAttribute("ErrormessageSDT", true);
            return link;
        }
        if (nhanViendb != null) {
            nhanViendb.setMaNV(nhanVien.getMaNV());
            nhanViendb.setHoTenNV(nhanVien.getHoTenNV());
            nhanViendb.setNgaySinh(nhanVien.getNgaySinh());
            nhanViendb.setSdtNV(nhanVien.getSdtNV());
            nhanViendb.setEmailNV(nhanVien.getEmailNV());
            nhanViendb.setGioiTinh(nhanVien.getGioiTinh());
            nhanViendb.setCCCDNV(nhanVien.getCCCDNV());
            nhanViendb.setDiaChi(nhanVien.getDiaChi());
            nhanViendb.setTrangThai(nhanVien.getTrangThai());
            nhanViendb.setChucVu(nhanVien.getChucVu());
            nhanViendb.setTgSua(new Date());
            nhanVienService.save(nhanViendb);
            redirectAttributes.addFlashAttribute("message", true);
        }
        return "redirect:/manage/nhan-vien";
    }

    @GetMapping("/nhanVien/filter")
    public String filterData(Model model,
                             @RequestParam(value = "maNV", required = false) String maNV,
                             @RequestParam(value = "tenNV", required = false) String tenNV) {
        // Thực hiện lọc dữ liệu dựa trên selectedSize (và trạng thái nếu cần)
        List<NhanVien> filteredNhanViens;
        if ("Mã Nhân Viên".equals(maNV) && "Tên Nhân Viên".equals(tenNV)) {
            // Nếu người dùng chọn "Tất cả", hiển thị tất cả dữ liệu
            filteredNhanViens = nhanVienService.getAllNhanVien();
        } else {
            // Thực hiện lọc dữ liệu dựa trên selectedSize
            filteredNhanViens = nhanVienService.fillterNhanVien(maNV, tenNV);
        }
        model.addAttribute("nhanVien", filteredNhanViens);
        model.addAttribute("nhanVienAll", nhanVienService.getAllNhanVien());

        return "manage/nhan-vien"; // Trả về mẫu HTML chứa bảng dữ liệu sau khi lọc
    }

    @PostMapping("/nhanVien/import")
    public String importData(@RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                InputStream excelFile = file.getInputStream();
                nhanVienService.importDataFromExcel(excelFile); // Gọi phương thức nhập liệu từ Excel
            } catch (Exception e) {
                e.printStackTrace();
                // Xử lý lỗi
            }
        }
        return "redirect:/manage/nhan-vien"; // Chuyển hướng sau khi nhập liệu thành công hoặc không thành công
    }

    @PutMapping("/nhan-vien/{idNV}")
    public ResponseEntity<String> capNhatTrangThai(@RequestParam String trangThai,
                                                   @PathVariable UUID idNV) {
        int trangThaiInt = Integer.valueOf(trangThai);

        int trangThaiUpdate;
        if (trangThaiInt == 1) {
            trangThaiUpdate = 0;
        } else {
            trangThaiUpdate = 1;
        }
        NhanVien nhanVien = nhanVienService.getByIdNhanVien(idNV);
        nhanVien.setTrangThai(trangThaiUpdate);
        nhanVien.setTgSua(new Date());
        nhanVienService.save(nhanVien);
        return ResponseEntity.ok("ok");
    }
}