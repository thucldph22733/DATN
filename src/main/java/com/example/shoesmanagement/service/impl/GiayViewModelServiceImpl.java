package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.repository.GiayViewModelRepository;
import com.example.shoesmanagement.service.GiayViewModelService;
import com.example.shoesmanagement.viewModel.GiayViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GiayViewModelServiceImpl implements GiayViewModelService {
    @Autowired
    private GiayViewModelRepository giayViewModelRepository;

    @Override
    public List<GiayViewModel> getAll(String name) {
        return giayViewModelRepository.searchByTenGiay(name);
    }

    @Override
    public GiayViewModel findByIdGiay(UUID id) {
        return giayViewModelRepository.findByIdGiay(id);
    }


    @Override
    public List<GiayViewModel> getAllVm() {
        return giayViewModelRepository.getAll();
    }

}
