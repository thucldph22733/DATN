package com.example.shoesmanagement.buyerController;

import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.GioHangChiTiet;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.service.GHCTService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class ContactController {
    private final JavaMailSender mailSender;

    public ContactController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Autowired
    private HttpSession session;
    @Autowired
    private GHCTService ghctService;

    @GetMapping("/contact")
    private String getContact(Model model) {
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null) {
            String fullName = khachHang.getHoTenKH();
            model.addAttribute("fullNameLogin", fullName);
            GioHang gioHang = (GioHang) session.getAttribute("GHLogged");

            List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);


            Integer sumProductInCart = listGHCTActive.size();
            model.addAttribute("sumProductInCart", sumProductInCart);
            model.addAttribute("heartLogged", true);

        } else {
            model.addAttribute("messageLoginOrSignin", true);
        }
        return "/online/contact";
    }


    @PostMapping("/contact/send")
    public String sendMess(@RequestParam String email,
                           @RequestParam String subject,
                           @RequestParam String message) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(email);
        smm.setTo("ngocnmph25730@fpt.edu.vn");
        smm.setSubject(subject);
        smm.setText(message);
        mailSender.send(smm);
        return "redirect:/buyer/contact";
    }
}
