package com.example.shoesmanagement.service;

import com.example.shoesmanagement.model.ChiTietGiay;

public interface CreateBarCodeService {

    void saveBarcodeImage(ChiTietGiay chiTietGiay, int width, int height);

    void deleteQRCode();

}

