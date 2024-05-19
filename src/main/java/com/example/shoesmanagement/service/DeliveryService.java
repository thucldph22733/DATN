package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.Province;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DeliveryService {
    private final ThanhPhoService thanhPhoService;
    @Autowired
    public DeliveryService(ThanhPhoService thanhPhoService) {
        this.thanhPhoService = thanhPhoService;
    }
    public LocalDate calculateDeliveryDate(String diaChi) {
        String[] parts = diaChi.split(",");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Địa chỉ không hợp lệ");
        }
        String thanhPho = parts[parts.length - 1].trim();

        Province province = thanhPhoService.findByNameProvince(thanhPho);
        if (province == null) {
            List<Province> provinceList = thanhPhoService.getAll();
            for (Province p : provinceList) {
                if (thanhPho.contains(p.getNameProvince())) {
                    province = p;
                    break;
                }
            }
        }

        if (province == null) {
            throw new IllegalArgumentException("Không tìm thấy tỉnh/thành phố phù hợp");
        }

        int transportCoefficient = province.getTransportCoefficient();
        return LocalDate.now().plus(transportCoefficient, ChronoUnit.DAYS);
    }
}
