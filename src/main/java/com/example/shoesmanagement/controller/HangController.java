package com.example.shoesmanagement.controller;


import com.example.shoesmanagement.model.Giay;
import com.example.shoesmanagement.model.Hang;
import com.example.shoesmanagement.model.HinhAnh;
import com.example.shoesmanagement.repository.HangRepository;
import com.example.shoesmanagement.service.GiayService;
import com.example.shoesmanagement.service.HangService;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RequestMapping("/manage")
@Controller
public class HangController {

    @Autowired
    private HangService hangService;

    @Autowired
    private GiayService giayService;

    @Autowired
    private GiayController giayController;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private HangRepository hangRepository;

    @Autowired
    private HttpSession session;

    @ModelAttribute("dsTrangThai")
    public Map<Integer, String> getDsTrangThai() {
        Map<Integer, String> dsTrangThai = new HashMap<>();
        dsTrangThai.put(1, "Hoạt động");
        dsTrangThai.put(0, "Không hoạt động");
        return dsTrangThai;
    }
    @GetMapping("/hang")
    public String getAllHang(Model model, @ModelAttribute("message") String message,
                             @ModelAttribute("maHangError") String maHangError,
                             @ModelAttribute("tenHangError") String tenHangError,
                             @ModelAttribute("error") String error,
                             @ModelAttribute("errorMessage") String errorMessage,
                             @ModelAttribute("userIput") Hang userInput) {
        List<Hang> hang = hangService.getALlHang();
        model.addAttribute("hang", hang);
        model.addAttribute("addHang", new Hang());
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login

            return "redirect:/login";

        }
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (maHangError == null || !"maHangError".equals(error)) {
            model.addAttribute("maHangError", false);
        }
        if (tenHangError == null || !"tenHangError".equals(error)) {
            model.addAttribute("tenHangError", false);
        }
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        if (userInput != null) {
            model.addAttribute("hangAdd", userInput);
        }
        //
        if (errorMessage == null || !"true".equals(errorMessage)) {
            model.addAttribute("errorMessage", false);
        }
        return "manage/hang";

    }

    @PostMapping("/hang/viewAdd/add")
    public String addHang(@RequestParam("maHang") String maHang,
                          @RequestParam("tenHang") String tenHang,
                          @RequestParam("logoHang") MultipartFile logoHang,
                          @Valid @ModelAttribute("hang") Hang hang, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        Hang existingHang = hangRepository.findByMaHang(hang.getMaHang());
        if (existingHang != null) {
            redirectAttributes.addFlashAttribute("useInput", hang);
            redirectAttributes.addFlashAttribute("errorMessage",true);
            return "redirect:/manage/hang";
        }
        Path path = Paths.get("src/main/resources/static/images/logoBrands/");
        Hang hang1 = new Hang();
        hang1.setMaHang(maHang);
        hang1.setTenHang(tenHang);
        if (logoHang.isEmpty()){
            return "redirect:/manage/hang";
        }
        try{
            InputStream inputStream = logoHang.getInputStream();
            Files.copy(inputStream,path.resolve(logoHang.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
            hang1.setLogoHang(logoHang.getOriginalFilename().toLowerCase());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        hang1.setTgThem(new Date());
        hang1.setTrangThai(1);
        hangService.save(hang1);
        redirectAttributes.addFlashAttribute("message",true);
        return "redirect:/manage/hang";
    }

    @GetMapping("/hang/delete/{id}")
    public String deleteHang(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        Hang hang = hangService.getByIdHang(id);
        hang.setTrangThai(0);
        hang.setTgSua(new Date());
        hangService.save(hang);
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/hang";
    }

    @GetMapping("/hang/viewUpdate/{id}")
    public String viewUpdateHang(@PathVariable UUID id, Model model
            , @ModelAttribute("message") String message
            , @ModelAttribute("maHangError") String maHangError
            , @ModelAttribute("tenHangError") String tenHangError
            , @ModelAttribute("error") String error, @ModelAttribute("userInput") Hang userInput
            , @ModelAttribute("Errormessage") String Errormessage) {
        Hang hang = hangService.getByIdHang(id);
        model.addAttribute("hang", hang);
        //
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (maHangError == null || !"maHangError".equals(error)) {
            model.addAttribute("maHangError", false);
        }
        if (tenHangError == null || !"tenHangError".equals(error)) {
            model.addAttribute("tenHangError", false);
        }
        if (userInput != null) {
            model.addAttribute("hangAdd", userInput);
        }
        httpSession.setAttribute("id", id);
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/update-hang";
    }

    @PostMapping("/hang/viewUpdate/{id}")
    public String updateHang(@RequestParam("maHang") String maHang,
                             @RequestParam("tenHang") String tenHang,
                             @RequestParam("logoHang") MultipartFile logoHang,
                             @PathVariable UUID id, @Valid @ModelAttribute("hang") Hang hang, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Hang hangDb = hangService.getByIdHang(id);
        UUID idHang = (UUID) httpSession.getAttribute("id");
        String link = "redirect:/manage/hang/viewUpdate/" + idHang;
        Hang existingHang = hangRepository.findByMaHang(hang.getMaHang());
        if (existingHang != null && !existingHang.getIdHang().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", hang);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return link;
        }
        Path path = Paths.get("src/main/resources/static/images/logoBrands/");
        if (hangDb != null) {
            hangDb.setMaHang(maHang);
            hangDb.setTenHang(tenHang);
            try {
                InputStream inputStream = logoHang.getInputStream();
                Files.copy(inputStream, path.resolve(logoHang.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);
                hangDb.setLogoHang(logoHang.getOriginalFilename().toLowerCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
            hangDb.setTgSua(new Date());
            hangDb.setTrangThai(hang.getTrangThai());
            hangService.save(hangDb);
            redirectAttributes.addFlashAttribute("message", true);
        }
        if (hangDb.getTrangThai() == 1) {
            List<Giay> giays = giayService.findByHang(hangDb);
            for (Giay giay : giays) {
                giay.setTrangThai(1);
                giayService.save(giay);
//                giayController.updateGiayById(giay.getIdGiay());
            }
        }
        return "redirect:/manage/hang";
    }

    @GetMapping("/hang/filter")
    public String filterData(Model model,
                             @RequestParam(value = "maHang", required = false) String maHang,
                             @RequestParam(value = "tenHang", required = false) String tenHang) {
        List<Hang> filteredHangs;
        if ("Mã Hãng".equals(maHang) && "Tên Hãng".equals(tenHang)) {
            filteredHangs = hangService.getALlHang();
        } else {
            filteredHangs = hangService.fillterHang(maHang, tenHang);
        }
        model.addAttribute("hang", filteredHangs);
        model.addAttribute("hangAll", hangService.getALlHang());
        return "manage/hang";
    }

    @PostMapping("/hang/import")
    public String importData(@RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                InputStream excelFile = file.getInputStream();
                hangService.importDataFromExcel(excelFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/manage/hang";
    }

    @PutMapping("/hang/{idHang}")
    public ResponseEntity<String> capNhatTrangThai(@RequestParam String trangThai,
                                                   @PathVariable UUID idHang) {
        int trangThaiInt = Integer.valueOf(trangThai);

        int trangThaiUpdate;
        if (trangThaiInt == 1) {
            trangThaiUpdate = 0;
        } else {
            trangThaiUpdate = 1;
        }
        Hang hang = hangService.getByIdHang(idHang);
        hang.setTrangThai(trangThaiUpdate);
        hang.setTgSua(new Date());
        hangService.save(hang);
        return ResponseEntity.ok("ok");
    }
}
