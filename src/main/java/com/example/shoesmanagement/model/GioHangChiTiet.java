package com.example.shoesmanagement.model;


import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.GioHang;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name= "Gio_Hang_Chi_Tiet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GioHangChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_GHCT")
    private UUID idGHCT;

    @ManyToOne
    @JoinColumn(name = "id_GH")
    private GioHang gioHang;

    @ManyToOne
    @JoinColumn(name = "id_CTG")
    private ChiTietGiay chiTietGiay;

    @Column(name = "so_Luong")
    private int soLuong;

    @Column(name = "don_Gia")
    private double donGia;

    @Column(name = "trang_Thai")
    private int trangThai;

    @Column(name = "tg_Them")
    private Date tgThem;

    @Column(name = "tg_Sua")
    private Date tgSua;
}
