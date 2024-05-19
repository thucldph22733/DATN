package com.example.shoesmanagement.repository;

import com.example.shoesmanagement.model.GiaoHang;
import com.example.shoesmanagement.model.ViTriDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public interface ViTriDonHangRepository extends JpaRepository<ViTriDonHang, UUID> {
    List<ViTriDonHang> findByGiaoHangOrderByThoiGianDesc(GiaoHang giaoHang);
}
