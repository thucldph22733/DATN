package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.HoaDon;

public interface ShippingFeeService {
    Double calculatorShippingFee(HoaDon hoaDon, Double giaTriMacDinh);
    Integer tinhNgayNhanDuKien (String tinhNgayNhanDuKien);

}
