package com.example.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name= "Hoa_Don")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_HD")
    private UUID idHD;

    @ManyToOne
    @JoinColumn(name = "id_KH")
    private KhachHang khachHang;

    @OneToMany(mappedBy = "hoaDon")
    private List<HoaDonChiTiet> hoaDonChiTiets;

    @OneToMany(mappedBy = "hoaDon")
    private List<LichSuThanhToan> lichSuThanhToans;

    @OneToOne
    @JoinColumn(name = "id_Giao_Hang")
    private GiaoHang giaoHang;

    @ManyToOne
    @JoinColumn(name = "id_NV")
    private NhanVien nhanVien;

    @Column(name = "ma_HD")
    private String maHD;

    @Column(name="tong_Tien_san_pham")
    private Double tongTienSanPham;

    @Column(name="tong_SP")
    private Integer tongSP;

    @Column(name = "tien_Ship")
    private Double tienShip;

    @Column(name ="tong_Tien")
    private Double tongTien;

    @Column(name = "tg_Tao")
    private Date tgTao;

    @Column(name = "trang_Thai")
    private int trangThai;

    @Column(name="loai_HD")
    private Integer loaiHD;

    @Column(name="hinh_Thuc_Thanh_Toan")
    private Integer hinhThucThanhToan;

    @Column(name = "so_lan_thay_doi_vi_tri_nhan")
    private Integer soLanThayDoiViTriShip;

}
