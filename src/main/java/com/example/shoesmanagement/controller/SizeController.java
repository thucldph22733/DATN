package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.Size;
import com.example.shoesmanagement.repository.SizeRepository;
import com.example.shoesmanagement.service.GiayChiTietService;
import com.example.shoesmanagement.service.SizeService;
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
public class SizeController {
    @Autowired
    private SizeService sizeService;
    @Autowired
    private GiayChiTietService giayChiTietService;
    @Autowired
    private HttpSession session;
    @Autowired
    private SizeRepository repository;


    // Cúc cuuuuu
    @ModelAttribute("dsTrangThai")
    public Map<Integer, String> getDsTrangThai() {
        Map<Integer, String> dsTrangThai = new HashMap<>();
        dsTrangThai.put(0, "Không Hoạt Động");
        dsTrangThai.put(1, "Hoạt Động");
        return dsTrangThai;
    }

    @ModelAttribute("currentTime")
    public Date getCurrentTime() {
        return new Date();
    }

    @GetMapping("/size")
    public String dsSize(Model model, @ModelAttribute("message") String message
            , @ModelAttribute("maSizeError") String maSizeError
            , @ModelAttribute("soSizeError") String soSizeError
            , @ModelAttribute("error") String error
            , @ModelAttribute("userInput") Size userInput
            , @ModelAttribute("Errormessage") String Errormessage
            , @ModelAttribute("trungSoSize") String trungSoSize) {
        List<Size> size = sizeService.getAllSize();
        model.addAttribute("size", size);
        //
        model.addAttribute("sizeAll", sizeService.getAllSize());
        //
        model.addAttribute("sizeAdd", new Size());
        //
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (maSizeError == null || !"maSizeError".equals(error)) {
            model.addAttribute("maSizeError", false);
        }
        if (soSizeError == null || !"soSizeError".equals(error)) {
            model.addAttribute("soSizeError", false);
        }
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        if (userInput != null) {
            model.addAttribute("sizeAdd", userInput);
        }
        //
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        //
        if (trungSoSize == null || !"true".equals(trungSoSize)) {
            model.addAttribute("trungSoSize", false);
        }
        return "manage/size-giay";
    }

//    @GetMapping("/size/viewAdd")
//    public String viewAddSize(Model model) {
//        model.addAttribute("size", new Size());
//        return "manage/add-size";
//    }

    @PostMapping("/size/viewAdd/add")
    public String addSize(@Valid @ModelAttribute("size") Size size, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("maSize")) {
                redirectAttributes.addFlashAttribute("userInput", size);
                redirectAttributes.addFlashAttribute("error", "maSizeError");
            }
            if (bindingResult.hasFieldErrors("soSize")) {
                redirectAttributes.addFlashAttribute("userInput", size);
                redirectAttributes.addFlashAttribute("error", "soSizeError");
            }
            return "redirect:/manage/size";
        }
        Size existingSize = repository.findByMaSize(size.getMaSize());
        if (existingSize != null) {
            redirectAttributes.addFlashAttribute("userInput", size);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return "redirect:/manage/size";
        }
        int soSize = size.getSoSize();
        Size size1 = repository.findBySoSize(soSize);
        if (size != null && size1 == null) {
            Size sizeAdd = new Size();
            sizeAdd.setMaSize(size.getMaSize());
            sizeAdd.setSoSize(size.getSoSize());
            sizeAdd.setTgThem(new Date());
            sizeAdd.setTrangThai(1);
            sizeService.save(sizeAdd);
        } else {
            redirectAttributes.addFlashAttribute("userInput", size);
            redirectAttributes.addFlashAttribute("trungSoSize", true);
            return "redirect:/manage/size";
        }
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/size";
    }

    @GetMapping("/size/delete/{id}")
    public String deleteSize(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        Size size = sizeService.getByIdSize(id);
        List<ChiTietGiay> chiTietGiayList = giayChiTietService.findBySize(size);
        //
        size.setTrangThai(0);
        size.setTgSua(new Date());
        sizeService.save(size);
        // Cập nhật trạng thái của tất cả sản phẩm chi tiết của hãng thành 0
        for (ChiTietGiay chiTietGiay : chiTietGiayList) {
            chiTietGiay.setTrangThai(0);
            giayChiTietService.save(chiTietGiay);
        }
        //
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/size";
    }

    @GetMapping("/size/viewUpdate/{id}")
    public String viewUpdateSize(@PathVariable UUID id, Model model
            , @ModelAttribute("message") String message
            , @ModelAttribute("importError") String importError
            , @ModelAttribute("maSizeError") String maSizeError
            , @ModelAttribute("soSizeError") String soSizeError
            , @ModelAttribute("error") String error
            , @ModelAttribute("userInput") Size userInput
            , @ModelAttribute("Errormessage") String Errormessage) {
        Size size = sizeService.getByIdSize(id);
        model.addAttribute("size", size);
        //
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (importError == null || !"true".equals(importError)) {
            model.addAttribute("importError", false);
        }
        if (maSizeError == null || !"maSizeError".equals(error)) {
            model.addAttribute("maSizeError", false);
        }
        if (soSizeError == null || !"soSizeError".equals(error)) {
            model.addAttribute("soSizeError", false);
        }
        // Kiểm tra xem có dữ liệu người dùng đã nhập không và điền lại vào trường nhập liệu
        if (userInput != null) {
            model.addAttribute("sizeAdd", userInput);
        }
        //
        session.setAttribute("idSize", id);
        //
        //
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/update-size";
    }

    @PostMapping("/size/viewUpdate/{id}")
    public String updateSize(@PathVariable UUID id, @Valid @ModelAttribute("size") Size size, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        Size sizeDb = sizeService.getByIdSize(id);
        UUID idSize = (UUID) session.getAttribute("idSize");
        String link = "redirect:/manage/size/viewUpdate/" + idSize;
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("maSize")) {
                redirectAttributes.addFlashAttribute("userInput", size);
                redirectAttributes.addFlashAttribute("error", "maSizeError");
            }
            if (bindingResult.hasFieldErrors("soSize")) {
                redirectAttributes.addFlashAttribute("userInput", size);
                redirectAttributes.addFlashAttribute("error", "soSizeError");
            }
            return link;
        }
        Size existingSize = repository.findByMaSize(size.getMaSize());
        if (existingSize != null && !existingSize.getIdSize().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", size);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return link;
        }
        if (sizeDb != null) {
            sizeDb.setMaSize(size.getMaSize());
            sizeDb.setSoSize(size.getSoSize());
            sizeDb.setTgSua(new Date());
            sizeDb.setTrangThai(size.getTrangThai());
            sizeService.save(sizeDb);
            redirectAttributes.addFlashAttribute("message", true);
        }
        if (sizeDb.getTrangThai() == 1) {
            List<ChiTietGiay> chiTietGiays = giayChiTietService.findBySize(sizeDb);
            for (ChiTietGiay chiTietGiay : chiTietGiays) {
                chiTietGiay.setTrangThai(1);
                giayChiTietService.save(chiTietGiay);
            }
        }
        return "redirect:/manage/size";
    }



    @GetMapping("/size/filter")
    public String filterData(Model model,
                             @RequestParam(value = "selectedSize", required = false) Integer selectedSize,
                             @RequestParam(value = "maSize", required = false) String maSize) {
        // Thực hiện lọc dữ liệu dựa trên selectedSize (và trạng thái nếu cần)
        List<Size> filteredSizes;
        if (selectedSize == null && "Mã Size".equals(maSize)) {
            // Nếu người dùng chọn "Tất cả", hiển thị tất cả dữ liệu
            filteredSizes = sizeService.getAllSize();
        } else {
            // Thực hiện lọc dữ liệu dựa trên selectedSize
            filteredSizes = sizeService.filterSizes(selectedSize, maSize);
        }
        model.addAttribute("size", filteredSizes);
        model.addAttribute("sizeAll", sizeService.getAllSize());

        return "manage/size-giay"; // Trả về mẫu HTML chứa bảng dữ liệu sau khi lọc
    }


}
