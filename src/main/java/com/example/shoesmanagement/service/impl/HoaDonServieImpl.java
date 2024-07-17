package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.repository.GiayChiTietRepository;
import com.example.shoesmanagement.repository.HoaDonChiTietRepository;
import com.example.shoesmanagement.repository.HoaDonRepository;
import com.example.shoesmanagement.service.GiayChiTietService;
import com.example.shoesmanagement.service.HoaDonService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
@Service
public class HoaDonServieImpl implements HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private GiayChiTietService chiTietGiayService;

    @Override
    public List<HoaDon> getListHoaDonChuaThanhToan() {
        return hoaDonRepository.listChuaThanhToan();
    }

    @Override
    public List<HoaDon> getListHoaDonDaThanhToan() {
        return hoaDonRepository.listDaThanhToan();
    }

    @Override
    public void add(HoaDon hoaDon) {
        hoaDonRepository.save(hoaDon);

    }
    @Override
    public void save(HoaDon hoaDon) {
        hoaDonRepository.save(hoaDon);
    }

    @Override
    public HoaDon getOne(UUID id) {
        return hoaDonRepository.findById(id).orElse(null);
    }

    @Override
    public List<HoaDon> listHoaDonKhachHangAndTrangThaiOnline(KhachHang khachHang, int trangThai) {
        return  hoaDonRepository.findByKhachHangAndLoaiHDAndTrangThaiOrderByTgTaoDesc(khachHang, 0,trangThai);
    }

    @Override
    public List<HoaDon> findHoaDonByKhachHang(KhachHang khachHang) {
        return hoaDonRepository.findByKhachHangAndLoaiHDOrderByTgTaoDesc(khachHang,0);
    }

    @Override
    public List<HoaDon> listHoaDonOnline() {
        return hoaDonRepository.findByLoaiHDOrderByTgTaoDesc(0);
    }

    @Override
    public List<HoaDon> listHoaDonByNhanVienAndTrangThai(NhanVien nhanVien, int trangThai) {
        return hoaDonRepository.findByNhanVienAndLoaiHDAndTrangThaiOrderByTgTaoDesc(nhanVien,0,trangThai);
    }

    @Override
    public void deleteHoaDonCho(UUID id) {
        Optional<HoaDon> optionalHoaDon = hoaDonRepository.findById(id);
        if (optionalHoaDon.isPresent()) {
            HoaDon hoaDon = optionalHoaDon.get();
            if (hoaDon.getTrangThai() == 0 && hoaDon.getLoaiHD() == 1) {
                // Lấy tất cả chi tiết hóa đơn liên quan đến hóa đơn
                List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByIdHoaDon(id);
                for (HoaDonChiTiet chiTiet : chiTietList) {
                    // Lấy chi tiết giày liên quan đến chi tiết hóa đơn
                    ChiTietGiay chiTietGiay = chiTiet.getChiTietGiay();
                    if (chiTietGiay != null) {
                        // Cập nhật số lượng sản phẩm trong chi tiết giày
                        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() + chiTiet.getSoLuong());
                        chiTietGiayService.save(chiTietGiay);
                    }
                    // Xóa chi tiết hóa đơn
                    hoaDonChiTietRepository.delete(chiTiet);
                }
                // Xóa hóa đơn
                hoaDonRepository.delete(hoaDon);
            } else {
                throw new IllegalArgumentException("Hóa đơn không ở trạng thái chờ hoặc không phải loại hóa đơn chờ");
            }
        } else {
            throw new NoSuchElementException("Không tìm thấy hóa đơn với ID: " + id);
        }
    }



    public double getTongTienSanPham(UUID idHoaDon) {
        // Lấy hóa đơn từ repository
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));

        // Tính tổng tiền sản phẩm
        return hoaDon.getHoaDonChiTiets().stream()
                .mapToDouble(hdc -> hdc.getDonGia() * hdc.getSoLuong())
                .sum();
    }

    @Override
    public List<HoaDon> getAllHoaDonOffLine() {
        return hoaDonRepository.findHoaDonByLoaiHDOrderByTgTaoDesc(1);
    }

    @Override
    public void updateHoaDon(HoaDon hoaDon) {
        double tongTienSanPham = 0;
        int tongSP = 0;

        for (HoaDonChiTiet hdc : hoaDon.getHoaDonChiTiets()) {
            tongTienSanPham += hdc.getDonGia() * hdc.getSoLuong();
            tongSP += hdc.getSoLuong();
        }

        hoaDon.setTongTienSanPham(tongTienSanPham);
        hoaDon.setTongSP(tongSP);

        hoaDonRepository.save(hoaDon);
    }


    @Override
    public List<HoaDon> listHoaDonHuyAndThanhCongByNhanVien(NhanVien nhanVien) {
        return hoaDonRepository.findByNhanVienAndLoaiHDAndTrangThaiOrTrangThaiOrderByTgTaoDesc(nhanVien,0,3,4);
    }

    @Override
    public List<HoaDon> listAllHoaDonByNhanVien(NhanVien nhanVien) {
        return hoaDonRepository.findByNhanVienOrderByTgTaoDesc(nhanVien);
    }

    @Override
    public List<HoaDon> listAllHoaDonByNhanVienHienTai(NhanVien nhanVien) {
        return hoaDonRepository.listAllHoaDonByNhanVienHienTai();
    }

    @Transactional
    @Override
    public void deleteByChiTietGiay(ChiTietGiay chiTietGiay) {
        hoaDonChiTietRepository.deleteByChiTietGiay(chiTietGiay);
    }
}
