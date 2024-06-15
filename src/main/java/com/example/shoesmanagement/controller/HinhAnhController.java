package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.HinhAnh;
import com.example.shoesmanagement.model.MauSac;
import com.example.shoesmanagement.repository.HinhAnhRepository;
import com.example.shoesmanagement.service.HinhAnhService;
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
public class HinhAnhController {

    @Autowired
    private HinhAnhService hinhAnhService;

    @Autowired
    private HinhAnhRepository hinhAnhRepository;

    @Autowired
    private HttpSession session;

    @ModelAttribute("dsTrangThai")
    public Map<Integer,String> getDSTrangThai(){
        Map<Integer,String>dsTrangThai = new HashMap<>();
        dsTrangThai.put(1,"Hoạt Động");
        dsTrangThai.put(2,"Không Hoạt Động");
        return dsTrangThai;
    }

    @GetMapping("/hinh-anh")
    public String dsHinhAnh(Model model, @ModelAttribute("message") String message,
                            @ModelAttribute("hinhAnhError") String maHinhAnhError,
                            @ModelAttribute("error") String error,
                            @ModelAttribute("userInput")HinhAnh userInput,
                            @ModelAttribute("errorMessage")String Errormessage){
        List<HinhAnh> listHinhAnh = hinhAnhService.getAllHinhAnh();
        model.addAttribute("hinhAnh",listHinhAnh);
        model.addAttribute("hinhAnhAdd", new HinhAnh());

        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
return "redirect:/login";
        }
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (maHinhAnhError == null || !"maHinhAnhError".equals(error)) {
            model.addAttribute("maHinhAnhError", false);
        }
        if (userInput != null) {
            model.addAttribute("hinhAnhAdd", userInput);
        }
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/hinh-anh";
    }

    @PostMapping("/hinh-anh/viewAdd/add")
    public String addHinhAnh(@RequestParam("maAnh") String maAnh,
                             @RequestParam("anh1") MultipartFile anh1,
                             @RequestParam("anh2") MultipartFile anh2,
                             @RequestParam("anh3") MultipartFile anh3,
                             @RequestParam("anh4") MultipartFile anh4,
                             @Valid @ModelAttribute("hinhAnh") HinhAnh hinhAnh, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes){
        HinhAnh existingAnh = hinhAnhRepository.findByMaAnh(hinhAnh.getMaAnh());
        if (existingAnh != null) {
            redirectAttributes.addFlashAttribute("userInput", hinhAnh);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return "redirect:/manage/hinh-anh";
        }
        HinhAnh hinhAnh1 = new HinhAnh();
        hinhAnh1.setMaAnh(maAnh);
        if (anh1.isEmpty() || anh2.isEmpty() || anh3.isEmpty() || anh4.isEmpty()) {
            return "redirect:/manage/hinh-anh";
        }
        Path path = Paths.get("src/main/resources/static/images/imgsProducts/");
        try {
            InputStream inputStream = anh1.getInputStream();
            Files.copy(inputStream, path.resolve(anh1.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
            hinhAnh1.setUrl1(anh1.getOriginalFilename().toLowerCase());
            inputStream = anh2.getInputStream();
            Files.copy(inputStream, path.resolve(anh2.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
            hinhAnh1.setUrl2(anh2.getOriginalFilename().toLowerCase());
            inputStream = anh3.getInputStream();
            Files.copy(inputStream, path.resolve(anh3.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
            hinhAnh1.setUrl3(anh3.getOriginalFilename().toLowerCase());
            inputStream = anh4.getInputStream();
            Files.copy(inputStream, path.resolve(anh4.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
            hinhAnh1.setUrl4(anh4.getOriginalFilename().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
        hinhAnh1.setTgThem(new Date());
        hinhAnh1.setTrangThai(1);
        hinhAnhService.save(hinhAnh1);
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/hinh-anh";
    }
    @GetMapping("/hinh-anh/delete/{id}")
    public String deleteHinhAnh(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        HinhAnh hinhAnh = hinhAnhService.getByIdHinhAnh(id);
        hinhAnh.setTrangThai(0);
        hinhAnh.setTgSua(new Date());
        hinhAnhService.save(hinhAnh);
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/hinh-anh";
    }

    @GetMapping("/hinh-anh/viewUpdate/{id}")
    public String viewUpdateHinhAnh(@PathVariable UUID id, Model model
            , @ModelAttribute("message") String message
            , @ModelAttribute("maHinhAnhError") String maHinhAnhError
            , @ModelAttribute("error") String error, @ModelAttribute("userInput") HinhAnh userInput, @ModelAttribute("Errormessage") String Errormessage) {
        HinhAnh hinhAnh = hinhAnhService.getByIdHinhAnh(id);
        model.addAttribute("hinhAnh", hinhAnh);
        //
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (maHinhAnhError == null || !"maHinhAnhError".equals(error)) {
            model.addAttribute("maHinhAnhError", false);
        }
        if (userInput != null) {
            model.addAttribute("hinhAnhAdd", userInput);
        }
        //
        session.setAttribute("id", id);
        //
        //
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/update-hinh-anh";
    }

    @PostMapping("/hinh-anh/viewUpdate/{id}")
    public String updateHinhAnh(@RequestParam("maAnh") String maAnh,
                                @RequestParam("anh1") MultipartFile anh1,
                                @RequestParam("anh2") MultipartFile anh2,
                                @RequestParam("anh3") MultipartFile anh3,
                                @RequestParam("anh4") MultipartFile anh4,
                                @PathVariable UUID id, @Valid @ModelAttribute("hinhAnh") HinhAnh hinhAnh, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        HinhAnh hinhAnhDb = hinhAnhService.getByIdHinhAnh(id);
        UUID idAnh = (UUID) session.getAttribute("id");
        String link = "redirect:/manage/hinh-anh/viewUpdate/" + idAnh;
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("maAnh")) {
                redirectAttributes.addFlashAttribute("userInput", hinhAnh);
                redirectAttributes.addFlashAttribute("error", "maHinhAnhError");
            }
            return link;
        }
        //
        HinhAnh existingAnh = hinhAnhRepository.findByMaAnh(hinhAnh.getMaAnh());
        if (existingAnh != null && !existingAnh.getIdHinhAnh().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", hinhAnh);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return link;
        }
        //
        Path path = Paths.get("src/main/resources/static/images/imgsProducts/");
        //
        if (hinhAnhDb != null) {
            hinhAnhDb.setMaAnh(maAnh);
            try {
                InputStream inputStream = anh1.getInputStream();
                Files.copy(inputStream, path.resolve(anh1.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);
                hinhAnhDb.setUrl1(anh1.getOriginalFilename().toLowerCase());
                //
                inputStream = anh2.getInputStream();
                Files.copy(inputStream, path.resolve(anh2.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);
                hinhAnhDb.setUrl2(anh2.getOriginalFilename().toLowerCase());
                //
                inputStream = anh3.getInputStream();
                Files.copy(inputStream, path.resolve(anh3.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);
                hinhAnhDb.setUrl3(anh3.getOriginalFilename().toLowerCase());
                //
                inputStream = anh4.getInputStream();
                Files.copy(inputStream, path.resolve(anh4.getOriginalFilename()),
                        StandardCopyOption.REPLACE_EXISTING);
                hinhAnhDb.setUrl4(anh4.getOriginalFilename().toLowerCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
            hinhAnhDb.setTgSua(new Date());
            hinhAnhDb.setTrangThai(hinhAnh.getTrangThai());
            hinhAnhService.save(hinhAnhDb);
            redirectAttributes.addFlashAttribute("message", true);
        }
        return "redirect:/manage/hinh-anh";
    }

    @GetMapping("/hinh-anh/filter")
    public String filterData(Model model, @RequestParam(value = "maMau", required = false) String ma) {
        List<HinhAnh> filteredHinhAnhs;
        if ("Mã".equals(ma)) {
            filteredHinhAnhs = hinhAnhService.getAllHinhAnh();
        } else {
            filteredHinhAnhs = hinhAnhService.filterHinhAnh(ma);
        }
        model.addAttribute("hinhAnh", filteredHinhAnhs);
        model.addAttribute("hinhAnhAll", hinhAnhService.getAllHinhAnh());

        return "manage/hinh-anh";
    }

    @PutMapping("/hinh-anh/{idHinhAnh}")
    public ResponseEntity<String> capNhatTrangThai(@RequestParam String trangThai,
                                                   @PathVariable UUID idHinhAnh) {
        int trangThaiInt = Integer.valueOf(trangThai);

        int trangThaiUpdate;
        if (trangThaiInt == 1) {
            trangThaiUpdate = 0;
        } else {
            trangThaiUpdate = 1;
        }
        HinhAnh hinhAnh = hinhAnhService.getByIdHinhAnh(idHinhAnh);
        hinhAnh.setTrangThai(trangThaiUpdate);
        hinhAnh.setTgSua(new Date());
        hinhAnhService.save(hinhAnh);
        return ResponseEntity.ok("ok");
    }
}
