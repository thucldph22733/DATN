package com.example.shoesmanagement.model;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name= "Khuyen_Mai")
@Data

public class KhuyenMai implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_Khuyen_Mai")
    private UUID idKM;


    @Column(name = "ma_KM")
    private String maKM;

    @Column(name = "ten_KM")
    private String tenKM;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tg_Bat_Dau")
    private Date tgBatDau;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tg_Ket_Thuc")
    private Date tgKetThuc;

    @Column(name = "mo_Ta")
    private String moTa;

    @Column(name = "so_Luong")
    private int soLuong;

    @Column(name = "so_Luong_Da_Dung")
    private int soLuongDaDung;

    @Column(name = "gia_Tien_Giam")
    private Double giaTienGiam;

    @Column(name = "dk_KM")
    private Double dieuKienKMBill;

    @Column(name = "trang_Thai")
    private int trangThai;


    @Column(name = "tg_Them")
    private Date tgThem;
}
