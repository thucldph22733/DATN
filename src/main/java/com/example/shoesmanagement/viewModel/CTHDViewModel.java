package com.example.shoesmanagement.viewModel;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CTHDViewModel {
    @Column(name = "gia_ban")
    private Double giaBan;
    @Column(name = "ten_giay")
    private String tenGiay;
    @Column(name = "don_gia")
    private Double donGia;
    @Column(name = "so_luong")
    private Integer soLuong;
    @Column(name = "url1")
    private String url1;
    @Column(name = "url1")
    private String ten;
    private String sdt;
    private String diaChi;

}
