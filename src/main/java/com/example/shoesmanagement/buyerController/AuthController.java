package com.example.shoesmanagement.buyerController;

import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.service.EmailService;
import com.example.shoesmanagement.service.GioHangService;
import com.example.shoesmanagement.service.KhachHangService;
import com.example.shoesmanagement.service.impl.EmailServiceImpl;
import com.example.shoesmanagement.service.impl.ForgotPasswordServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Controller
@RequestMapping("/buyer")
public class AuthController {
    @Autowired
    private KhachHangService khachHangService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpSession session;
    @Autowired
    private GioHangService gioHangService;
    @Autowired
    private EmailServiceImpl emailService;

    private Random random = new Random();
    @Autowired
    private ForgotPasswordServiceImpl forgotPasswordService;


    @GetMapping("/login")
    public String getFormBuyerLogin(){
        return "online/login";
    }
    @GetMapping("/register")
    public String getFormBuyerRegister(Model model) {
        KhachHang khachHang = new KhachHang();
        model.addAttribute("formRegister", true);
        model.addAttribute("userRegister", khachHang);
        return "online/register";
    }

    @GetMapping("/logout")
    public String getFormBuyerLogout() {
        session.invalidate();
        return "redirect:/buyer/";
    }


    @GetMapping("/forgotpassword")
    public String showForgotPasswordForm() {
        return "online/forgotPassword";
    }

    @PostMapping("/forgotpassword/sendpassword")
    public ResponseEntity<String> sendNewPassword(
            @RequestParam("email") String email,
            @RequestParam("newPass") String newPass,
            @RequestParam("reNewPass") String reNewPass) {

        if (!newPass.equals(reNewPass)) {
            return ResponseEntity.badRequest().body("Mật khẩu mới không khớp.");
        }

        forgotPasswordService.sendNewPassword(email, newPass); // Gửi mật khẩu mới vào email

        return ResponseEntity.ok("Mật khẩu mới đã được gửi tới email của bạn.");
    }

    @PostMapping("/forgotpassword/reset")
    public String handleResetPassword(
            @RequestParam("email") String email,
            @RequestParam("code") String code,
            @RequestParam("newPass") String newPass,
            @RequestParam("reNewPass") String reNewPass,
            Model model) {

        if (forgotPasswordService.verifyCode(email, code)) {
            if (newPass != null && newPass.equals(reNewPass)) {
                forgotPasswordService.resetPassword(email, newPass);
                model.addAttribute("message", "Mật khẩu của bạn đã được đặt lại thành công.");
                return "login";
            } else {
                model.addAttribute("error", "Mật khẩu mới không khớp.");
                return "online/forgotPassword";
            }
        } else {
            model.addAttribute("error", "Mã xác nhận không đúng.");
            return "online/forgotPassword";
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> buyerLogin(Model model, HttpSession session) throws MessagingException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        Date date = new Date();
        Boolean b = username.matches(EMAIL_REGEX);
        if (b) {
            KhachHang khachHang = khachHangService.checkLoginEmail(username, password);
            if (khachHang != null) {
                GioHang gh = gioHangService.findByKhachHang(khachHang);
                if (gh != null) {
                    String fullName = khachHang.getHoTenKH();
                    model.addAttribute("fullNameLogin", fullName);
                    session.setAttribute("KhachHangLogin", khachHang);
                    session.setAttribute("GHLogged", gh);
                    return ResponseEntity.ok("Login thanh cong");
                }
                GioHang gioHang = new GioHang();
                gioHang.setKhachHang(khachHang);
                gioHang.setTrangThai(1);
                gioHang.setTgThem(date);
                gioHangService.saveGH(gioHang);
                session.setAttribute("KhachHangLogin", khachHang);
                session.setAttribute("GHLogged", gioHang);
                return ResponseEntity.ok("Login thanh cong");

            } else {
                return ResponseEntity.ok("Tk and mk sai");
            }
        } else {
            return ResponseEntity.ok("Tk and mk sai");
        }
    }



    @PostMapping("/register")
    public String buyerRegister(Model model, RedirectAttributes redirectAttributes) {
        String fullName = request.getParameter("fullname");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        boolean hasErr = false;

        if (fullName == null || "".equals(fullName)) {
            model.addAttribute("messFullname", "Not Blank  !");
            hasErr = true;
        }
        if (email == null || "".equals(email)) {
            model.addAttribute("messEmail", "Not Blank  !");
            hasErr = true;
        }
        if (hasErr) {
            model.addAttribute("messageFullName", fullName);
            model.addAttribute("messageEmail", email);
            model.addAttribute("formRegister", true);
            return "/online/register";
        }

        KhachHang kh = khachHangService.checkEmail(email);
        if (kh != null) {
            model.addAttribute("messEmail", "Email already exists ! ");
            model.addAttribute("messageFullName", fullName);
            model.addAttribute("formRegister", true);
            return "/online/register";
        }
        Date date = new Date();
        Integer sequenceNumber = (Integer) session.getAttribute("sequenceNumber");
        if (sequenceNumber == null) {
            sequenceNumber = 1; // Khởi tạo nếu chưa có trong session
        }

        // Tạo mã hóa đơn với ngày hôm nay và số thứ tự
        SimpleDateFormat formatter = new SimpleDateFormat("ddMM");
        String strDate = formatter.format(date);
        String maKH = "KH_" + strDate + "_" + sequenceNumber;

        // Tăng số thứ tự và lưu lại trong session
        sequenceNumber++;
        session.setAttribute("sequenceNumber", sequenceNumber);

        KhachHang khachHang = new KhachHang();
        khachHang.setEmailKH(email);
        khachHang.setHoTenKH(fullName);
        khachHang.setMatKhau(password);
        khachHang.setTrangThai(1);
        khachHang.setMaKH(maKH);
        khachHang.setTgThem(date);
        khachHangService.save(khachHang);
        session.setAttribute("userRegister", khachHang);
        model.addAttribute("formRegister", false);
        return "online/login";
    }
    public String generateRandomNumbers() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int ramdumNumber = random.nextInt(10);
            sb.append(ramdumNumber);
        }
        return sb.toString();
    }

}
