package com.example.shoesmanagement.buyerController;

import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.GioHangChiTiet;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.service.GHCTService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.AbstractDocument;
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
//        return "/online/c";
    }


    @PostMapping("/contact/send")
    public String sendMess(@RequestParam String emailFrom,
                           @RequestParam String emailTo,
                           @RequestParam String subject,
                           @RequestParam String message) {
//        Email from = new Email(emailFrom);
//        Email to = new Email(emailTo);
//        AbstractDocument.Content content = new Content("text/plain", message);
//        Mail mail = new Mail(from, subject, to, content);
//
//        SendGrid sg = new SendGrid(sendGridApiKey);
//        Request request = new Request();
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            Response response = sg.api(request);
//            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
//                return "redirect:/buyer/contact";
//            } else {
//                return "redirect:/buyer/contact?error";
//            }
//        } catch (IOException ex) {
//            // Xử lý lỗi nếu có
            return "redirect:/buyer/contact?error";
        }
    }

