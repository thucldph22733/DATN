package com.example.shoesmanagement.service;

import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {
    public String createOrder(Integer total, String orderInfor, String urlReturn);

    public int orderReturn(HttpServletRequest request);
}
