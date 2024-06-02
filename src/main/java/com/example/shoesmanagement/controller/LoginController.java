package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.NhanVien;
import com.example.shoesmanagement.service.ChucVuService;
import com.example.shoesmanagement.service.NhanVienService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class LoginController {

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;


    @GetMapping("/login")
    private String getLoginAll() {

        return "/login";
    }



    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpSession session) {
        session.invalidate();
        request.getSession().invalidate();
        return "redirect:/login";
    }

    @PostMapping("/login")
    private String nhanVienLogin(Model model) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

        Boolean b = username.matches(EMAIL_REGEX);


        if (b) {


            NhanVien nhanVien = nhanVienService.checkByEmailAndPass(username, password);
            if (nhanVien != null) {
                if (nhanVien.getChucVu().getMaCV().equalsIgnoreCase("AD")) {
                    session.setAttribute("managerLogged", nhanVien);
                    return "redirect:/manage/";
                } else if (nhanVien.getChucVu().getMaCV().equalsIgnoreCase("CV01")) {
                    session.setAttribute("staffLogged", nhanVien);
                    return "redirect:/ban-hang/hien-thi";
                } else if (nhanVien.getChucVu().getMaCV().equalsIgnoreCase("CV02")) {
                    session.setAttribute("staffLogged", nhanVien);
                    return "redirect:/ban-hang/hien-thi";
                }else{
                    model.addAttribute("messageLogin", "Not Access");
                    return "/login";
                }
            } else {
                model.addAttribute("messageLogin", "Username or Password incorrect");
                return "/login";
            }
        } else {
            NhanVien nhanVien = nhanVienService.checkBySDTAndPass(username, password);
            if (nhanVien != null) {
                if (nhanVien.getChucVu().getMaCV().equalsIgnoreCase("AD")) {
                    session.setAttribute("managerLogged", nhanVien);
                    return "redirect:/manage/";
                } else if (nhanVien.getChucVu().getMaCV().equalsIgnoreCase("NV01")) {
                    session.setAttribute("staffLogged", nhanVien);
                    return "redirect:/ban-hang/hien-thi";
                }else if (nhanVien.getChucVu().getMaCV().equalsIgnoreCase("NV02")) {
                    session.setAttribute("staffLogged", nhanVien);
                    return "redirect:/ban-hang/hien-thi";
                } else{
                    model.addAttribute("messageLogin", "Not Access");
                    return "/login";
                }
            } else {
                model.addAttribute("messageLogin", "Username or Password incorrect");
                return "/login";
            }
        }
    }
}
