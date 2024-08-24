package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.GioHangChiTiet;
import com.example.shoesmanagement.model.HoaDon;
import com.example.shoesmanagement.model.HoaDonChiTiet;
import com.example.shoesmanagement.repository.HoaDonChiTietRepository;
import com.example.shoesmanagement.repository.HoaDonRepository;
import com.example.shoesmanagement.service.HoaDonChiTIetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HoaDonChiTietServiceImpl implements HoaDonChiTIetService {
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Override
    public HoaDonChiTiet getOne(UUID idHoaDon, UUID idChiTietGiay) {
        return hoaDonChiTietRepository.findByIdHoaDonAndIdChiTietGiay(idHoaDon, idChiTietGiay);
    }

    @Override
    public HoaDonChiTiet findByCTSPActive(ChiTietGiay chiTietGiay) {
        return hoaDonChiTietRepository.findByChiTietGiayAndTrangThai(chiTietGiay,1);
    }


    @Override
    public void delete(HoaDonChiTiet hoaDonChiTiet) {

    }

    @Override
    public void add(HoaDonChiTiet hoaDonChiTiet) {
        hoaDonChiTietRepository.save(hoaDonChiTiet);

    }



    @Override
    public void save(HoaDonChiTiet hoaDonChiTiet) {
        hoaDonChiTietRepository.save(hoaDonChiTiet);

    }

    @Override
    public List<HoaDonChiTiet> findByIdHoaDon(UUID id) {
        return hoaDonChiTietRepository.findByIdHoaDon(id);
    }

    @Override
    public Double tongTien(List<HoaDonChiTiet> list) {
        double tongTien = 0;
        for (HoaDonChiTiet hoaDonChiTiet : list) {
            if (hoaDonChiTiet.getTrangThai() == 1) {
                tongTien += hoaDonChiTiet.getDonGia();
            }
        }
        return tongTien;
    }
    public List<HoaDonChiTiet> getListByHoaDon(UUID idHD) {
        return hoaDonChiTietRepository.findByHoaDon_IdHD(idHD);
    }
    @Override
    public Double tongTienSanPham(List<HoaDonChiTiet> list) {
        double tongTienSanPham = 0;
        for (HoaDonChiTiet hoaDonChiTiet : list) {
            if (hoaDonChiTiet.getTrangThai() == 1) {
                tongTienSanPham += hoaDonChiTiet.getDonGia();
            }
        }
        return tongTienSanPham;
    }

    @Override

    public void updateQuantity(UUID idHDCT, int newQuantity) {
        Optional<HoaDonChiTiet> optionalHoaDonChiTiet = hoaDonChiTietRepository.findById(idHDCT);

        if (optionalHoaDonChiTiet.isPresent()) {
            HoaDonChiTiet hoaDonChiTiet = optionalHoaDonChiTiet.get();
            hoaDonChiTiet.setSoLuong(newQuantity);
//            hoaDonChiTiet.setDonGia(newDonGia);

            // Cập nhật lại tổng tiền sản phẩm và tổng số lượng sản phẩm trong hóa đơn
            HoaDon hoaDon = hoaDonChiTiet.getHoaDon();
//            updateHoaDonTotals(hoaDon);
            hoaDonChiTietRepository.save(hoaDonChiTiet);
        } else {
            throw new RuntimeException("Không tìm thấy HoaDonChiTiet với id: " + idHDCT);
        }
    }

    @Override
    public List<HoaDonChiTiet> findByHoaDon(HoaDon hoaDon) {
        return hoaDonChiTietRepository.findByHoaDon(hoaDon);
    }

    @Override
    public void updateQuantityGiay(UUID idHDCT, int newQuantity) {
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietRepository.findById(idHDCT).orElse(null);
        if (hoaDonChiTiet != null) {
            hoaDonChiTiet.setSoLuong(newQuantity);
            hoaDonChiTietRepository.save(hoaDonChiTiet);
            // Sau khi cập nhật số lượng, cần cập nhật lại tổng tiền sản phẩm của hóa đơn
        }
    }

    @Override
    public HoaDonChiTiet findByCTGActiveAndKhachHagAndTrangThai(ChiTietGiay chiTietGiay, HoaDon hoaDon) {
        return hoaDonChiTietRepository.findByChiTietGiayAndTrangThaiAndHoaDon(chiTietGiay, 1, hoaDon);
    }

    @Override
    public void deleteCTG(UUID idCTG, UUID idHD) {
        hoaDonChiTietRepository.deleteHoaDonChiTietByChiTietGiay(idCTG,idHD);
    }

    @Override
    public void deleteChiTietGiay(UUID idHDCT) {
        hoaDonChiTietRepository.deleteById(idHDCT);
    }
}