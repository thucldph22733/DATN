package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.ChucVu;
import com.example.shoesmanagement.model.NhanVien;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface NhanVienService {
    NhanVien checkByEmailAndPass(String email, String pass);

    NhanVien checkBySDTAndPass(String sdt, String pass);

    public List<NhanVien> getAllNhanVien();

    public void save(NhanVien nhanVien);

    public void deleteByIdNhanVien(UUID id);

    public NhanVien getByIdNhanVien(UUID id);

    public void importDataFromExcel(InputStream excelFile);


    public List<NhanVien> findByChucVu(ChucVu chucVu);

    public List<NhanVien> findByTrangThai(int trangThai);

    public List<NhanVien> fillterNhanVien(String maNV, String tenNV);

   NhanVien findByEmailNV(String emailNV) ;

}
