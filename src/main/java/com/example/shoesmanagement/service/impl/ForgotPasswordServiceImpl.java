package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.repository.KhachHangRepository;
import com.example.shoesmanagement.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private KhachHangRepository khachHangRepository;

    private Map<String, String> verificationCodes = new HashMap<>();
    @Autowired
    private EmailServiceImpl emailServiceImpl;

    public boolean sendVerificationCode(String email) {
        KhachHang khachHang = khachHangRepository.findByEmailKH(email);
        return khachHang != null;
    }

    public String generateVerificationCode() {
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));
        return code;
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(verificationCodes.get(email));
    }

    public void resetPassword(String email, String newPassword) {
        KhachHang khachHang = khachHangRepository.findByEmailKH(email);
        if (khachHang != null) {
            khachHang.setMatKhau(newPassword);
            khachHangRepository.save(khachHang);
        }
    }
    @Override
    public void sendNewPassword(String email, String newPassword) {
        // Gửi mật khẩu mới vào email của người dùng
        String subject = "Mật khẩu mới của bạn";
        String content = "Mật khẩu mới của bạn là: " + newPassword;
        emailServiceImpl.sendSimpleMessage(email, subject, content);
    }
}
