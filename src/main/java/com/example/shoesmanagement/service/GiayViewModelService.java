package com.example.shoesmanagement.service;

import com.example.shoesmanagement.viewModel.GiayViewModel;

import java.util.List;
import java.util.UUID;

public interface GiayViewModelService {
    List<GiayViewModel> getAll(String name);

    GiayViewModel findByIdGiay(UUID id);
    List<GiayViewModel> getAllVm();
}
