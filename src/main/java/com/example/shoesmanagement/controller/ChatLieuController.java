package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.ChatLieu;
import com.example.shoesmanagement.model.Giay;
import com.example.shoesmanagement.repository.ChatLieuRepository;
import com.example.shoesmanagement.service.ChatLieuService;
import com.example.shoesmanagement.service.GiayService;
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

@RequestMapping("/manage")
@Controller
public class ChatLieuController {

    @Autowired
    private ChatLieuService chatLieuService;

    @Autowired
    private ChatLieuRepository chatLieuRepository;

    @Autowired
    GiayService giayService;

    @Autowired
    GiayController giayController;

    @Autowired
    HttpSession session;

    @ModelAttribute("dsTrangThai")
    public Map<Integer, String> getDSTrangThai() {
        Map<Integer, String> dSTrangThai = new HashMap<>();
        dSTrangThai.put(1, "Hoạt Động");
        dSTrangThai.put(0, "Không Hoạt Động");
        return dSTrangThai;
    }

    @GetMapping("/chat-lieu")
    public String dsChatLieu(Model model, @ModelAttribute("message") String message
            , @ModelAttribute("maChatLieuError") String maChatLieuError
            , @ModelAttribute("tenChatLieuError") String tenChatLieuError
            , @ModelAttribute("error") String error, @ModelAttribute("userInput") ChatLieu userInput, @ModelAttribute("Errormessage") String Errormessage) {

        List<ChatLieu> chatLieu = chatLieuService.getAllChatLieu();
        model.addAttribute("chatLieu", chatLieu);
        model.addAttribute("chatLieuAdd", new ChatLieu());
        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login

            return "redirect:/login";

        }
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (maChatLieuError == null || !"maChatLieuError".equals(error)) {
            model.addAttribute("maChatLieuError", false);
        }
        if (tenChatLieuError == null || !"tenChatLieuError".equals(error)) {
            model.addAttribute("tenChatLieuError", false);
        }
        if (userInput != null) {
            model.addAttribute("chatLieuAdd", userInput);
        }
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/chat-lieu";
    }

    @PostMapping("/chat-lieu/viewAdd/add")
    public String addChatLieu(@Valid @ModelAttribute("chatLieu") ChatLieu chatLieu, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("maChatLieu")) {
                redirectAttributes.addFlashAttribute("userInput", chatLieu);
                redirectAttributes.addFlashAttribute("error", "maChatLieuError");
            }
            if (bindingResult.hasFieldErrors("tenChatLieu")) {
                redirectAttributes.addFlashAttribute("userInput", chatLieu);
                redirectAttributes.addFlashAttribute("error", "tenChatLieuError");
            }
            return "redirect:/manage/chat-lieu";
        }
        ChatLieu exitingChatLieu = chatLieuRepository.findByMaChatLieu(chatLieu.getMaChatLieu());
        if (exitingChatLieu != null) {
            redirectAttributes.addFlashAttribute("userInput", chatLieu);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return "redirect:/manage/chat-lieu";
        }
        ChatLieu chatLieu1 = new ChatLieu();
        chatLieu1.setMaChatLieu(chatLieu.getMaChatLieu());
        chatLieu1.setTenChatLieu(chatLieu.getTenChatLieu());
        chatLieu1.setTgThem(new Date());
        chatLieu1.setTrangThai(1);
        chatLieuService.save(chatLieu1);
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/chat-lieu";
    }

    @GetMapping("/chat-lieu/delete/{id}")
    public String deleteChatLieu(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        ChatLieu chatLieu = chatLieuService.getByIdChatLieu(id);
        chatLieu.setTrangThai(0);
        chatLieu.setTgSua(new Date());
        List<Giay> listGiay = giayService.findByChatLieu(chatLieu);
        for (Giay giay : listGiay) {
            giay.setTrangThai(0);
            giayService.save(giay);
//            giayController.deleteGiayById(giay.getIdGiay());
        }
        redirectAttributes.addFlashAttribute("message", true);
        return "redirect:/manage/chat-lieu";
    }

    @GetMapping("/chat-lieu/viewUpdate/{id}")
    public String viewUpdateChatLieu(@PathVariable UUID id, Model model
            , @ModelAttribute("message") String message
            , @ModelAttribute("maChatLieuError") String maChatLieuError
            , @ModelAttribute("tenChatLieuError") String tenChatLieuError
            , @ModelAttribute("error") String error, @ModelAttribute("userInput") ChatLieu userInput
            , @ModelAttribute("Errormessage") String Errormessage) {
        ChatLieu chatLieu = chatLieuService.getByIdChatLieu(id);
        model.addAttribute("chatLieu", chatLieu);
        if (message == null || !"true".equals(message)) {
            model.addAttribute("message", false);
        }
        if (maChatLieuError == null || !"maChatLieuError".equals(error)) {
            model.addAttribute("maChatLieuError", false);
        }
        if (tenChatLieuError == null || !"tenChatLieuError".equals(error)) {
            model.addAttribute("tenChatLieuError", false);
        }
        if (userInput != null) {
            model.addAttribute("chatLieuAdd", userInput);
        }
        session.setAttribute("id", id);
        if (Errormessage == null || !"true".equals(Errormessage)) {
            model.addAttribute("Errormessage", false);
        }
        return "manage/update-chat-lieu";
    }

    @PostMapping("/chat-lieu/viewUpdate/{id}")
    public String updateChatLieu(@PathVariable UUID id, @Valid @ModelAttribute("chatLieu") ChatLieu chatLieu, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ChatLieu chatLieuDb = chatLieuService.getByIdChatLieu(id);
        UUID idChatLieu = (UUID) session.getAttribute("id");
        String link = "redirect:/manage/chat-lieu/viewUpdate/" + idChatLieu;
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("maChatLieu")) {
                redirectAttributes.addFlashAttribute("userInput", chatLieu);
                redirectAttributes.addFlashAttribute("error", "maChatLieuError");
            }
            if (bindingResult.hasFieldErrors("tenChatLieu")) {
                redirectAttributes.addFlashAttribute("userInput", chatLieu);
                redirectAttributes.addFlashAttribute("error", "tenChatLieuError");
            }
            return link;
        }
        //
        ChatLieu existingChatLieu = chatLieuRepository.findByMaChatLieu(chatLieu.getMaChatLieu());
        if (existingChatLieu != null && !existingChatLieu.getIdChatLieu().equals(id)) {
            redirectAttributes.addFlashAttribute("userInput", chatLieu);
            redirectAttributes.addFlashAttribute("Errormessage", true);
            return link;
        }
        //
        if (chatLieuDb != null) {
            chatLieuDb.setMaChatLieu(chatLieu.getMaChatLieu());
            chatLieuDb.setTenChatLieu(chatLieu.getTenChatLieu());
            chatLieuDb.setTgSua(new Date());
            chatLieuDb.setTrangThai(chatLieu.getTrangThai());
            chatLieuService.save(chatLieuDb);
            redirectAttributes.addFlashAttribute("message", true);
        }
        if (chatLieuDb.getTrangThai() == 1) {
            List<Giay> giays = giayService.findByChatLieu(chatLieuDb);
            for (Giay giay : giays) {
                giay.setTrangThai(1);
                giayService.save(giay);
//                giayController.updateGiayById(giay.getIdGiay());
            }
        }
        return "redirect:/manage/chat-lieu";
    }

    @PutMapping("/chat-lieu/{idChatLieu}")
    public ResponseEntity<String> capNhatTrangThai(@RequestParam String trangThai,
                                                   @PathVariable UUID idChatLieu) {
        int trangThaiInt = Integer.valueOf(trangThai);
        System.out.println(trangThai);

        int trangThaiUpdate;
        if (trangThaiInt == 1) {
            trangThaiUpdate = 0;
        } else {
            trangThaiUpdate = 1;
        }
        System.out.println(trangThaiUpdate);
        ChatLieu chatLieu = chatLieuService.getByIdChatLieu(idChatLieu);
        chatLieu.setTrangThai(trangThaiUpdate);
        chatLieuService.save(chatLieu);
        return ResponseEntity.ok("ok");
    }
}
