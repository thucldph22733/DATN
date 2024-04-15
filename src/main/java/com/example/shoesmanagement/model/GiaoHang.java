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
@Table(name= "Giao_Hang")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GiaoHang {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_Giao_Hang")
    private UUID idGH;

    @OneToMany(mappedBy = "giaoHang")
    private List<ViTriDonHang> viTriDonHangs;

    @OneToOne
    @JoinColumn(name = "id_HD")
    private HoaDon hoaDon;

    @Column(name = "ma_giao_hang")
    private String maGiaoHang;

    @Column(name="trang_Thai")
    private int trangThai;

    @Column(name = "thoi_Gian")
    private Date thoiGian;

    @Column(name ="noi_Dung")
    private String noiDung;

    @Column(name ="ten_dvvc")
    private String tenDVVC;

    @Column(name ="ma_van_don")
    private String maVanDon;

    @Column(name ="ma_van_don_hoan")
    private String maVanDonHoan;

    @Column(name ="phi_giao_hang")
    private Double phiGiaoHang;

    @Column(name ="phi_hoan_hang")
    private Double phiHoanHang;

    @Column(name = "thoi_Gian_Hoan")
    private Date thoiGianHoan;

    @Column(name = "tg_ThanhToan")
    private Date tgThanhToan;

    @Column(name = "tg_Ship")
    private Date tgShip;

    @Column(name = "tg_Nhan")
    private Date tgNhan;

    @Column(name = "tg_Huy")
    private Date tgHuy;

    @Column(name = "ly_do_huy")
    private String lyDoHuy;

    @Column(name = "tg_Nhan_DK")
    private Date tgNhanDK;

    @Column(name = "ten_Nguoi_Nhan")
    private String tenNguoiNhan;

    @Column(name = "dia_Chi_Nguoi_Nhan")
    private String diaChiNguoiNhan;

    @Column(name = "sdt_Nguoi_Nhan")
    private String sdtNguoiNhan;
}
