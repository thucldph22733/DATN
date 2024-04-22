package com.example.shoesmanagement.buyerController;

import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.GioHangChiTiet;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.service.GHCTService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class AboutController {
    @Autowired
    private HttpSession session;
    @Autowired
    private GHCTService ghctService;


    @GetMapping("/about")
    private String getAbout(Model model) {
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
        return "/online/about";
    }

}
