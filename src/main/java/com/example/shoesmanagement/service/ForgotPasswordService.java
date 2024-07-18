package com.example.shoesmanagement.service;

public interface ForgotPasswordService {
    boolean  sendVerificationCode(String email);
    boolean verifyCode(String email, String code);
    void resetPassword(String email, String newPassword);
    void sendNewPassword(String email, String newPassword);
}
