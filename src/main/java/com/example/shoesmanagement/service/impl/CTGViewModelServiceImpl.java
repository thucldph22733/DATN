package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.repository.CTGViewModelRepository;
import com.example.shoesmanagement.service.CTGViewModelService;
import com.example.shoesmanagement.viewModel.CTGViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CTGViewModelServiceImpl implements CTGViewModelService {


    @Autowired
    private CTGViewModelRepository ctgViewModelRepository;

    @Override
    public List<CTGViewModel> getAll() {
        return ctgViewModelRepository.getAll();
    }



    @Override
    public Page<CTGViewModel> getAllPage(Pageable pageable) {
        return ctgViewModelRepository.getAllPageable(pageable);
    }

    @Override
    public List<CTGViewModel> getAllSoldOff() {
        return ctgViewModelRepository.getAllOutOfStock();
    }



    @Override
    public CTGViewModel findByIDGiayAndMau(UUID idGiay, UUID idMau) {
        return ctgViewModelRepository.findByGiay(idGiay, idMau);
    }

    @Override
    public List<CTGViewModel> findByIDHang(UUID idHang) {
        return ctgViewModelRepository.findByHang(idHang);
    }

    @Override
    public Page<CTGViewModel> getAllByPriceHighToLow(Pageable pageable) {
        return ctgViewModelRepository.listCTGVMHTL(pageable);
    }

    @Override
    public Page<CTGViewModel> getAllByPriceLowToHigh(Pageable pageable) {
        return ctgViewModelRepository.listCTGVMLTH(pageable);
    }

    @Override
    public List<CTGViewModel> getAllOrderTgNhap() {
        return ctgViewModelRepository.getAllOrderTgNhap();
    }

    @Override
    public List<CTGViewModel> getAllOrderBestSeller() {
        return ctgViewModelRepository.getAllOrderBestSeller();
    }
}
