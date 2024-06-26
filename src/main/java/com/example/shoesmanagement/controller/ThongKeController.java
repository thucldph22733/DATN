package com.example.shoesmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manage")
public class ThongKeController {
    @Autowired
    private HttpSession session;

    @GetMapping("/thong-ke")
    public String hienThi(Model model, @ModelAttribute("message") String message
            , @ModelAttribute("error") String error, @ModelAttribute("Errormessage") String Errormessage) {

        if (session.getAttribute("managerLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/login";
        }
        return "manage/thong-ke";
    }
}
