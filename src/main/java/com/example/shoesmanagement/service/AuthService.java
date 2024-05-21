package com.example.shoesmanagement.service;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    Integer ChangePass(String email, String currentPass, String reNewPass, String newPass, HttpServletRequest request);
}
