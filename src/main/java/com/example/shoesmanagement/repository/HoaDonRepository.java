package com.example.shoesmanagement.repository;

import com.example.shoesmanagement.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;


@Repository
public interface HoaDonRepository  extends JpaRepository<HoaDon, UUID> {

    @Query(value = "select * from Hoa_Don where trang_thai = 0 and loai_hd = 1 ",nativeQuery = true)
    List<HoaDon> listChuaThanhToan();

    @Query(value = "select * from Hoa_Don where trang_thai = 1 and loai_hd = 1 ",nativeQuery = true)
    List<HoaDon> listDaThanhToan();

    @Query(value = "select COUNT(id_hd)\n" +
            "from hoa_don where trang_thai =1 ",nativeQuery = true)
    Integer getAllHoaDonDaThanhToan();

    @Query(value = "select COUNT(id_hd)\n" +
            "from hoa_don where trang_thai =0",nativeQuery = true)
    Integer getAllHoaDonCho();


    List<HoaDon> findHoaDonByLoaiHDOrderByTgTaoDesc(int loaiHoaDon);

    List<HoaDon> findByKhachHangAndLoaiHDOrderByTgTaoDesc(KhachHang khachHang, int loaiHD);

    List<HoaDon> findByKhachHangAndLoaiHDAndTrangThaiOrderByTgTaoDesc(KhachHang khachHang,int loaiHD, int trangThai);

    List<HoaDon> findByLoaiHDOrderByTgTaoDesc(int loaiHD);

    List<HoaDon> findByNhanVienAndLoaiHDAndTrangThaiOrderByTgTaoDesc(NhanVien nhanVien, int loaiHD, int trangThai);

    List<HoaDon> findByNhanVienAndLoaiHDAndTrangThaiOrTrangThaiOrderByTgTaoDesc(NhanVien nhanVien, int loaiHD, int trangThai1, int trangThai2);

    List<HoaDon> findByNhanVienOrderByTgTaoDesc(NhanVien nhanVien);

    @Query(value = "select * from hoa_don where day(tg_tao) = day(GETDATE()) and loai_hd=1 ",nativeQuery = true)
    List<HoaDon> listAllHoaDonByNhanVienHienTai();

    @Query("select km from KhuyenMai km left join HoaDon hd on km.idKM = hd.khuyenMai.idKM where km.dieuKienKMBill <= ?1 and km.trangThai= 1 and km.soLuong > 0")
    List<KhuyenMai> listDieuKienKhuyenMai(double tongTienSanPham);

    @Query("select hd from HoaDon hd where hd.loaiHD = 1 and hd.trangThai = ?1")
    List<HoaDon> locHoaDonOff(int trangThai);

    List<HoaDon> findByTrangThai(int trangThai);

}
