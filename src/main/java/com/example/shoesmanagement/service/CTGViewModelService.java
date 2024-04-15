package com.example.shoesmanagement.service;

import com.example.shoesmanagement.viewModel.CTGViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CTGViewModelService {
    List<CTGViewModel> getAll();


    Page<CTGViewModel> getAllPage(Pageable pageable);

    List<CTGViewModel> getAllSoldOff();

    CTGViewModel findByIDGiayAndMau(UUID idGiay, UUID idMau);

    List<CTGViewModel> findByIDHang(UUID idHang);

    Page<CTGViewModel> getAllByPriceHighToLow(Pageable pageable);

    Page<CTGViewModel> getAllByPriceLowToHigh(Pageable pageable);

    List<CTGViewModel> getAllOrderTgNhap();

    List<CTGViewModel> getAllOrderBestSeller();

}
