package com.example.shoesmanagement.repository;

import com.example.shoesmanagement.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GiayChiTietRepository extends JpaRepository<ChiTietGiay, UUID> {

    List<ChiTietGiay> findByGiay(Giay giay);

    List<ChiTietGiay> findByTrangThaiAndGiay(int trangThai, Giay giay);
    List<ChiTietGiay> findByGiayAndMauSac(Giay giay, MauSac mauSac);
    @Query(value = "SELECT DISTINCT ctg.hinhAnh FROM ChiTietGiay ctg WHERE ctg.giay = ?1 AND ctg.mauSac = ?2")
    HinhAnh findDistinctByGiay(Giay giay, MauSac mauSac);
    List<ChiTietGiay> findByMauSacAndGiayAndTrangThai(MauSac mauSac, Giay giay, int trangThai);


    List<ChiTietGiay> findBySize(Size size);

    List<ChiTietGiay> findByMauSac(MauSac mauSac);

    List<ChiTietGiay> findAllByOrderByTgThemDesc();

    ChiTietGiay findByMaCTG(String ma);

    @Query("SELECT c FROM ChiTietGiay c WHERE c.giay.idGiay = :giayId AND c.size.idSize = :sizeId AND c.mauSac.idMau = :mauSacId AND c.hinhAnh.idHinhAnh = :hinhAnhId")
    List<ChiTietGiay> findByGiayAndSizeAndMauSacAndHinhAnh(
            @Param("giayId") UUID giayId,
            @Param("sizeId") UUID sizeId,
            @Param("mauSacId") UUID mauSacId,
            @Param("hinhAnhId") UUID hinhAnhId
    );
    @Query("UPDATE ChiTietGiay c SET c.soLuong = :quantity WHERE c.idCTG = :id")
    void updateQuantity(@Param("id") UUID id, @Param("quantity") int quantity);

    @Query(value = "select sum(tong_sp) from hoa_don where trang_thai = 4",nativeQuery = true)
    Integer getTongGiay();


}
