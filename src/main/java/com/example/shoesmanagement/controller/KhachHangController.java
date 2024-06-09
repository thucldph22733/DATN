package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.DiaChiKH;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.repository.KhachHangRepository;
import com.example.shoesmanagement.service.DiaChiKHService;
import com.example.shoesmanagement.service.KhachHangService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/manage")
public class KhachHangController {
    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private DiaChiKHService diaChiKHService;

    @Autowired
    private HttpSession session;

    @ModelAttribute("dsTrangThai")
    public Map<Integer, String> getDsTrangThai() {
        Map<Integer, String> dsTrangThai = new HashMap<>();
        dsTrangThai.put(1, "Hoạt động");
        dsTrangThai.put(0, "Không hoạt động");
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

    @GetMapping("/khach-hang")
    public String dsKhachHang(Model model, @ModelAttribute("message") String message) {
        List<KhachHang> khachHangs = khachHangService.getAllKhachHang();
        model.addAttribute("khachHang", khachHangs);
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
return "redirect:/login";
        }
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        return "manage/khach-hang";
    }

    @GetMapping("/khach-hang/viewAdd")
    public String viewAddkhachHang(Model model
            , @ModelAttribute("errorKH") String errorKH
            , @ModelAttribute("userInput") KhachHang userInputKH
            , @ModelAttribute("messageLKH") String messageLKH
            , @ModelAttribute("Errormessage") String Errormessage
            , @ModelAttribute("ErrormessageLKH") String ErrormessageLKH) {
        //
        model.addAttribute("khachHang", new KhachHang());
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        if (userInputKH != null) {
            model.addAttribute("khachHang", userInputKH);
        }
        //add LKH
        if (messageLKH == null || !"true".equals(messageLKH)) {
            model.addAttribute("messageLKH", false);
        }
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        //
        if (ErrormessageLKH == null || !"true".equals(ErrormessageLKH)) {
            model.addAttribute("ErrormessageLKH", false);
        }

        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/add-khach-hang";
    }


    @PostMapping("/khach-hang/viewAdd/add")
    public String addkhachHang(@ModelAttribute("khachHang") KhachHang khachHang,Model model
            , RedirectAttributes redirectAttributes) {
        model.addAttribute("khachHang", new KhachHang());


        KhachHang existingKH = khachHangRepository.findByMaKH(khachHang.getMaKH());
        if (existingKH != null) {
            redirectAttributes.addFlashAttribute("userInput", khachHang);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return "redirect:/manage/khach-hang/viewAdd";
        }
        KhachHang existingKH1 = khachHangRepository.findByEmailKH(khachHang.getEmailKH());
        if (existingKH1 != null) {
            redirectAttributes.addFlashAttribute("userInput", khachHang);
            redirectAttributes.addFlashAttribute("ErrormessageeEmail", true);
            return "redirect:/manage/khach-hang/viewAdd";
        }
        KhachHang existingKH2 = khachHangRepository.findByCCCDKH(khachHang.getCCCDKH());
        if (existingKH2 != null) {
            redirectAttributes.addFlashAttribute("userInput", khachHang);
            redirectAttributes.addFlashAttribute("ErrormessageCCCD", true);
            return "redirect:/manage/khach-hang/viewAdd";
        }
        KhachHang existingKH3 = khachHangRepository.findBySdtKH(khachHang.getSdtKH());
        if (existingKH3 != null) {
            redirectAttributes.addFlashAttribute("userInput", khachHang);
            redirectAttributes.addFlashAttribute("ErrormessageSDT", true);
            return "redirect:/manage/khach-hang/viewAdd";
        }
        //
        KhachHang khachHang1 = new KhachHang();
        khachHang1.setMaKH(khachHang.getMaKH());
        khachHang1.setHoTenKH(khachHang.getHoTenKH());
        khachHang1.setDiaChi(khachHang.getDiaChi());
        khachHang1.setEmailKH(khachHang.getEmailKH());
        khachHang1.setMatKhau(khachHang.getMatKhau());
        khachHang1.setCCCDKH(khachHang.getCCCDKH());
        khachHang1.setSdtKH(khachHang.getSdtKH());
        khachHang1.setAnhKH(khachHang.getAnhKH());
        khachHang1.setAnhcccd(khachHang.getAnhcccd());
        khachHang1.setGioiTinh(khachHang.getGioiTinh());
        khachHang1.setNgaySinh(khachHang.getNgaySinh());
        khachHang1.setTgThem(new Date());
        khachHang1.setTrangThai(1);
        khachHangService.save(khachHang1);
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/khach-hang";
    }

    @GetMapping("/khach-hang/delete/{id}")
    public String deleteNhanVien(@PathVariable UUID id) {
        KhachHang khachHang = khachHangService.getByIdKhachHang(id);
        khachHang.setTrangThai(0);
        khachHang.setTgSua(new Date());
        khachHangService.save(khachHang);
        return "redirect:/manage/khach-hang";
    }

    public void deleteKHById(UUID idKH) {
        KhachHang khachHang = khachHangService.getByIdKhachHang(idKH);
        khachHang.setTrangThai(0);
        khachHang.setTgSua(new Date());
        khachHangService.save(khachHang);
    }

    @GetMapping("/khach-hang/viewUpdate/{id}")
    public String viewUpdatekhachhang(@PathVariable UUID id, Model model
            , @ModelAttribute("errorKH") String errorKH, @ModelAttribute("userInput") KhachHang userInputKH
            , @ModelAttribute("messageLKH") String messageLKH
            , @ModelAttribute("Errormessage") String Errormessage
            , @ModelAttribute("ErrormessageLKH") String ErrormessageLKH) {
        KhachHang khachHang = khachHangService.getByIdKhachHang(id);
        model.addAttribute("khachHang", khachHang);
        //
//        List<LoaiKhachHang> loaiKhachHangs = loaiKhachHangService.getAllLoaiKhachHang();
//        Collections.sort(loaiKhachHangs, (a, b) -> b.getTgThem().compareTo(a.getTgThem()));
//        model.addAttribute("loaiKhachHang", loaiKhachHangs);

//        model.addAttribute("loaiKhachHangAdd", new LoaiKhachHang());
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        if (userInputKH != null) {
            model.addAttribute("khachHangUpdate", userInputKH);
        }
        session.setAttribute("id", id);
        //
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/update-khach-hang";
    }

    @PostMapping("/khach-hang/viewUpdate/{id}")
    public String updatekhachhang(@PathVariable UUID id
            , @Valid @ModelAttribute("khachHang") KhachHang khachHang
            , BindingResult result, RedirectAttributes redirectAttributes) {
        KhachHang khachHangdb = khachHangService.getByIdKhachHang(id);
        UUID idKH = (UUID) session.getAttribute("id");
        String link = "redirect:/manage/khach-hang/viewUpdate/" + idKH;
        //
        KhachHang existingKH = khachHangRepository.findByMaKH(khachHang.getMaKH());
        if (existingKH != null && !existingKH.getIdKH().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", khachHang);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return link;
        }
        KhachHang existingKH1 = khachHangRepository.findByEmailKH(khachHang.getEmailKH());
        if (existingKH1 != null && !existingKH.getIdKH().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", khachHang);
            redirectAttributes.addFlashAttribute("ErrormessageeEmail", true);
            return link;
        }
        KhachHang existingKH2 = khachHangRepository.findByCCCDKH(khachHang.getCCCDKH());
        if (existingKH2 != null && !existingKH.getIdKH().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", khachHang);
            redirectAttributes.addFlashAttribute("ErrormessageCCCD", true);
            return link;
        }
        KhachHang existingKH3 = khachHangRepository.findBySdtKH(khachHang.getSdtKH());
        if (existingKH3 != null && !existingKH.getIdKH().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", khachHang);
            redirectAttributes.addFlashAttribute("ErrormessageSDT", true);
            return link;
        }

        if (khachHangdb != null ) {
            khachHangdb.setMaKH(khachHang.getMaKH());
            khachHangdb.setHoTenKH(khachHang.getHoTenKH());
            khachHangdb.setDiaChi(khachHang.getDiaChi());
            khachHangdb.setEmailKH(khachHang.getEmailKH());
            khachHangdb.setMatKhau(khachHang.getMatKhau());
            khachHangdb.setSdtKH(khachHang.getSdtKH());
            khachHangdb.setCCCDKH(khachHang.getCCCDKH());
            khachHangdb.setGioiTinh(khachHang.getGioiTinh());
            khachHangdb.setNgaySinh(khachHang.getNgaySinh());
            khachHangdb.setTgSua(new Date());
            khachHangdb.setTrangThai(khachHang.getTrangThai());
            khachHangService.save(khachHangdb);
            redirectAttributes.addFlashAttribute("message", true);
        }
        return "redirect:/manage/khach-hang";
    }

//    @GetMapping("/khachHang/export/pdf")
//    public void exportToPDFChatLieu(HttpServletResponse response) throws DocumentException, IOException {
//        response.setContentType("application/pdf");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=khachHang_" + currentDateTime + ".pdf";
//        response.setHeader(headerKey, headerValue);
//
//        List<KhachHang> listKhachHang = khachHangService.getAllKhachHang();
//
//        PDFExporterKhachHang exporter = new PDFExporterKhachHang(listKhachHang);
//        exporter.export(response);
//    }
    @GetMapping("/khach-hang/detail/{id}")
    public String detail(@PathVariable UUID id, Model model, @ModelAttribute("message") String message) {
        KhachHang khachHang = khachHangService.getByIdKhachHang(id);
        List<DiaChiKH> diaChiKHS = diaChiKHService.getDiaChibyKhachHang(khachHang);
        model.addAttribute("diaChiList", diaChiKHS);
        model.addAttribute("idDC", id);
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        session.setAttribute("idDC",id);
        return "manage/khach-hang-detail";
    }
//    @GetMapping("/khachHang/export/excel")
//    public void exportToExcelSize(HttpServletResponse response) throws IOException {
//        response.setContentType("application/octet-stream");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=khachHang_" + currentDateTime + ".xlsx";
//        response.setHeader(headerKey, headerValue);
//
//        List<KhachHang> listkhachHang = khachHangService.getAllKhachHang();
//
//        ExcelExporterKhachHang excelExporter = new ExcelExporterKhachHang(listkhachHang);
//
//        excelExporter.export(response);
//    }

    @GetMapping("/khachHang/filter")
    public String filterData(Model model,
                             @RequestParam(value = "maKH", required = false) String maKH,
                             @RequestParam(value = "tenKH", required = false) String tenKH) {
        // Thực hiện lọc dữ liệu dựa trên selectedSize (và trạng thái nếu cần)
        List<KhachHang> filteredKhachHangs;
        if ("Mã Khách Hàng".equals(maKH) && "Tên Khách Hàng".equals(tenKH)) {
            // Nếu người dùng chọn "Tất cả", hiển thị tất cả dữ liệu
            filteredKhachHangs = khachHangService.getAllKhachHang();
        } else {
            // Thực hiện lọc dữ liệu dựa trên selectedSize
            filteredKhachHangs = khachHangService.fillterKhachHang(maKH, tenKH);
        }
        model.addAttribute("khachHang", filteredKhachHangs);
        model.addAttribute("khachHangAll", khachHangService.getAllKhachHang());

        return "manage/khach-hang"; // Trả về mẫu HTML chứa bảng dữ liệu sau khi lọc
    }

//    @PostMapping("/khachHang/import")
//    public String importData(@RequestParam("file") MultipartFile file) {
//        if (file != null && !file.isEmpty()) {
//            try {
//                InputStream excelFile = file.getInputStream();
//                khachHangService.importDataFromExcel(excelFile); // Gọi phương thức nhập liệu từ Excel
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Xử lý lỗi
//            }
//        }
//        return "redirect:/manage/khach-hang"; // Chuyển hướng sau khi nhập liệu thành công hoặc không thành công
//    }
}
