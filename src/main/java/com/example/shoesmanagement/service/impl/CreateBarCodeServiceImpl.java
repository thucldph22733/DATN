package com.example.shoesmanagement.service.impl;

import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.service.CreateBarCodeService;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Service
public class CreateBarCodeServiceImpl implements CreateBarCodeService {

    @Override
    public void saveBarcodeImage(ChiTietGiay chiTietGiay, int width, int height) {
        try {
            String projectPath = System.getProperty("user.dir");
            String qrCodePath = projectPath + "/src/main/resources/static/images/imgsBarcode/";
//            String qrCodePath = getClass().getResource("src/main/resources/static/images/imgsBarcode/").getPath();
            String qrCodeName = qrCodePath + chiTietGiay.getIdCTG() + ".png";
            Color backgroundColor = Color.white;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    String.valueOf(chiTietGiay.getIdCTG()),
                    com.google.zxing.BarcodeFormat.QR_CODE,
                    width,
                    height,
                    null);
            BufferedImage qrCodeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrCodeImage.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : backgroundColor.getRGB());
                }
            }
            ImageIO.write(qrCodeImage, "png", new File(qrCodeName));
        } catch (Exception e) {
            System.out.printf("Lỗi " + e.getMessage());
        }
    }

    @Override
    public void deleteQRCode() {
        String projectPath = System.getProperty("user.dir");
        String qrCodePath = projectPath + "/src/main/resources/static/images/imgsBarcode/";
//        String qrCodePath = getClass().getResource("src/main/resources/static/images/imgsBarcode/").getPath();
        File directory = new File(qrCodePath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }
}
