package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.NhanVien;
import com.example.shoesmanagement.repository.NhanVienRepository;
import com.example.shoesmanagement.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthuServiceImpl implements AuthService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Override
    public Integer ChangePass(String email, String currentPass, String reNewPass, String newPass, HttpServletRequest request) {
        NhanVien nhanVien = nhanVienRepository.findByEmailNV(email);
            nhanVien.setMatKhau(newPass);
            nhanVienRepository.save(nhanVien);
            return 0;
        }
    }
