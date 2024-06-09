package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.MauSac;
import com.example.shoesmanagement.repository.MauSacRepository;
import com.example.shoesmanagement.service.GiayChiTietService;
import com.example.shoesmanagement.service.MauSacService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/manage")
public class MauSacController {
    @Autowired
    private MauSacService mauSacService;
    @Autowired
    private GiayChiTietService giayChiTietService;
    @Autowired
    private HttpSession session;
    @Autowired
    private MauSacRepository repository;

    @ModelAttribute("dsTrangThai")
    public Map<Integer, String> getDsTrangThai() {
        Map<Integer, String> dsTrangThai = new HashMap<>();
        dsTrangThai.put(1, "Hoạt động");
        dsTrangThai.put(0, "Không hoạt động");
        return dsTrangThai;
    }

    @GetMapping("/mau-sac")
    public String dsMauSac(Model model, @ModelAttribute("message") String message
            , @ModelAttribute("maError") String maError
            , @ModelAttribute("maMauError") String maMauError
            , @ModelAttribute("tenMauError") String tenMauError
            , @ModelAttribute("error") String error, @ModelAttribute("userInput") MauSac userInput, @ModelAttribute("Errormessage") String Errormessage) {

        List<MauSac> mauSac = mauSacService.getALlMauSac();
        model.addAttribute("mauSac", mauSac);
        //
        model.addAttribute("mauSacAdd", new MauSac());

        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
return "redirect:/login";
        }
        //
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (maError == null || !"maError".equals(error)) {
            model.addAttribute("maError", false);
        }
        if (maMauError == null || !"maMauError".equals(error)) {
            model.addAttribute("maMauError", false);
        }
        if (tenMauError == null || !"tenMauError".equals(error)) {
            model.addAttribute("tenMauError", false);
        }
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        if (userInput != null) {
            model.addAttribute("mauSacAdd", userInput);
        }
        //
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/mau-sac";
//        return "manage/activities";
    }

//    @GetMapping("/mau-sac/viewAdd")
//    public String viewAddMauSac(Model model) {
//        model.addAttribute("mauSac", new MauSac());
//        return "manage/add-mau-sac";
//    }

    @PostMapping("/mau-sac/viewAdd/add")
    public String addMauSac(@Valid @ModelAttribute("mauSac") MauSac mauSac, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("ma")) {
                redirectAttributes.addFlashAttribute("userInput", mauSac);
                redirectAttributes.addFlashAttribute("error", "maError");
            }
            if (bindingResult.hasFieldErrors("maMau")) {
                redirectAttributes.addFlashAttribute("userInput", mauSac);
                redirectAttributes.addFlashAttribute("error", "maMauError");
            }
            if (bindingResult.hasFieldErrors("tenMau")) {
                redirectAttributes.addFlashAttribute("userInput", mauSac);
                redirectAttributes.addFlashAttribute("error", "tenMauError");
            }
            return "redirect:/manage/mau-sac";
        }
        //
        MauSac existingMau = repository.findByMa(mauSac.getMa());
        if (existingMau != null) {
            redirectAttributes.addFlashAttribute("userInput", mauSac);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return "redirect:/manage/mau-sac";
        }
        //
        MauSac mauSac1 = new MauSac();
        mauSac1.setMa(mauSac.getMa());
        mauSac1.setMaMau(mauSac.getMaMau());
        mauSac1.setTenMau(mauSac.getTenMau());
        mauSac1.setTgThem(new Date());
        mauSac1.setTrangThai(1);
        mauSacService.save(mauSac1);
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/mau-sac";
    }

    @GetMapping("/mau-sac/delete/{id}")
    public String deleteMauSac(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        MauSac mauSac = mauSacService.getByIdMauSac(id);
        mauSac.setTrangThai(0);
        mauSac.setTgSua(new Date());
        mauSacService.save(mauSac);
        // Cập nhật trạng thái của tất cả sản phẩm chi tiết của mauSac thành 0
        List<ChiTietGiay> chiTietGiays = giayChiTietService.findByMauSac(mauSac);
        for (ChiTietGiay chiTietGiay : chiTietGiays) {
            chiTietGiay.setTrangThai(0);
            giayChiTietService.save(chiTietGiay);
        }
        //
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/mau-sac";
    }

    @GetMapping("/mau-sac/viewUpdate/{id}")
    public String viewUpdateMauSac(@PathVariable UUID id, Model model
            , @ModelAttribute("message") String message
            , @ModelAttribute("maError") String maError
            , @ModelAttribute("maMauError") String maMauError
            , @ModelAttribute("tenMauError") String tenMauError
            , @ModelAttribute("error") String error, @ModelAttribute("userInput") MauSac userInput, @ModelAttribute("Errormessage") String Errormessage) {
        MauSac mauSac = mauSacService.getByIdMauSac(id);
        model.addAttribute("mauSac", mauSac);
        //
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (maError == null || !"maError".equals(error)) {
            model.addAttribute("maError", false);
        }
        if (maMauError == null || !"maMauError".equals(error)) {
            model.addAttribute("maMauError", false);
        }
        if (tenMauError == null || !"tenMauError".equals(error)) {
            model.addAttribute("tenMauError", false);
        }
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        if (userInput != null) {
            model.addAttribute("mauSacAdd", userInput);
        }
        //
        session.setAttribute("id", id);
        //
        //
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/update-mau-sac";
    }

    @PostMapping("/mau-sac/viewUpdate/{id}")
    public String updateMauSac(@PathVariable UUID id, @Valid @ModelAttribute("mauSac") MauSac mauSac, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        MauSac mauSacDb = mauSacService.getByIdMauSac(id);
        UUID idMau = (UUID) session.getAttribute("id");
        String link = "redirect:/manage/mau-sac/viewUpdate/" + idMau;
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("ma")) {
                redirectAttributes.addFlashAttribute("userInput", mauSac);
                redirectAttributes.addFlashAttribute("error", "maError");
            }
            if (bindingResult.hasFieldErrors("maMau")) {
                redirectAttributes.addFlashAttribute("userInput", mauSac);
                redirectAttributes.addFlashAttribute("error", "maMauError");
            }
            if (bindingResult.hasFieldErrors("tenMau")) {
                redirectAttributes.addFlashAttribute("userInput", mauSac);
                redirectAttributes.addFlashAttribute("error", "tenMauError");
            }
            return link;
        }
        //
        MauSac existingMau = repository.findByMa(mauSac.getMa());
        if (existingMau != null && !existingMau.getIdMau().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", mauSac);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return link;
        }
        //
        if (mauSacDb != null) {
            mauSacDb.setMa(mauSac.getMa());
            mauSacDb.setMaMau(mauSac.getMaMau());
            mauSacDb.setTenMau(mauSac.getTenMau());
            mauSacDb.setTgSua(new Date());
            mauSacDb.setTrangThai(mauSac.getTrangThai());
            mauSacService.save(mauSacDb);
            redirectAttributes.addFlashAttribute("message", true);
        }
        // Nếu trạng thái của mauSac là 1, hãy cập nhật trạng thái của tất cả sản phẩm chi tiết của mauSac thành 1.
        if (mauSacDb.getTrangThai() == 1) {
            List<ChiTietGiay> chiTietGiays = giayChiTietService.findByMauSac(mauSacDb);
            for (ChiTietGiay chiTietGiay : chiTietGiays) {
                chiTietGiay.setTrangThai(1);
                giayChiTietService.save(chiTietGiay);
            }
        }
        return "redirect:/manage/mau-sac";
    }

    @GetMapping("/mauSac/filter")
    public String filterData(Model model,
                             @RequestParam(value = "maMau", required = false) String maMau,
                             @RequestParam(value = "tenMau", required = false) String tenMau) {
        // Thực hiện lọc dữ liệu dựa trên selectedSize (và trạng thái nếu cần)
        List<MauSac> filteredMauSacs;
        if ("Mã Màu".equals(maMau) && "Tên Màu".equals(tenMau)) {
            // Nếu người dùng chọn "Tất cả", hiển thị tất cả dữ liệu
            filteredMauSacs = mauSacService.getALlMauSac();
        } else {
            // Thực hiện lọc dữ liệu dựa trên selectedSize
            filteredMauSacs = mauSacService.filterMauSac(maMau, tenMau);
        }
        model.addAttribute("mauSac", filteredMauSacs);
        model.addAttribute("mauSacAll", mauSacService.getALlMauSac());

        return "manage/mau-sac"; // Trả về mẫu HTML chứa bảng dữ liệu sau khi lọc
    }

//    @PostMapping("/mauSac/import")
//    public String importData(@RequestParam("file") MultipartFile file) {
//        if (file != null && !file.isEmpty()) {
//            try {
//                InputStream excelFile = file.getInputStream();
//                mauSacService.importDataFromExcel(excelFile); // Gọi phương thức nhập liệu từ Excel
//            } catch (Exception e) {
//                e.printStackTrace();
//                // Xử lý lỗi
//            }
//        }
//        return "redirect:/manage/mau-sac"; // Chuyển hướng sau khi nhập liệu thành công hoặc không thành công
//    }

    @PutMapping("/mau-sac/{idMau}")
    public ResponseEntity<String> capNhatTrangThai(@RequestParam String trangThai,
                                                   @PathVariable UUID idMau) {
        int trangThaiInt = Integer.valueOf(trangThai);

        int trangThaiUpdate;
        if (trangThaiInt == 1) {
            trangThaiUpdate = 0;
        } else {
            trangThaiUpdate = 1;
        }
        System.out.println(trangThaiUpdate);
        MauSac mauSac = mauSacService.getByIdMauSac(idMau);
        mauSac.setTrangThai(trangThaiUpdate);
        mauSac.setTgSua(new Date());
        mauSacService.save(mauSac);
        return ResponseEntity.ok("ok");
    }
}