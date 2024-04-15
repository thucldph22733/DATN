package com.example.shoesmanagement.viewModel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CTGViewModel {
    @Id
    @Column(name = "id_giay")
    private UUID idGiay;

    @Column(name = "id_mau")
    private UUID idMau;

    @Column(name = "min_price_truoc_giam")
    private Double minPriceTruocGiam;

    @Column(name = "min_price")
    private Double minPrice;

    @Column(name = "ten_giay")
    private String tenGiay;

    @Column(name = "ten_mau")
    private String tenMau;

    @Column(name = "slTon")
    private Long slTon;

    @Column(name = "url1")
    private String hinhAnh;

    @Column(name = "so_Luong_Da_Ban")
    private Long soLuongDaBan;

    @Column(name = "tg_NhapHang")
    private Date tgNhapHang;


}




