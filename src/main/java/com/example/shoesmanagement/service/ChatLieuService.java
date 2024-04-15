package com.example.shoesmanagement.service;


import com.example.shoesmanagement.model.ChatLieu;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface ChatLieuService {
    public List<ChatLieu> getAllChatLieu();

    public void save(ChatLieu chatLieu);

    public void deleteByIdChatLieu(UUID id);

    public ChatLieu getByIdChatLieu(UUID id);

    public List<ChatLieu> fillterChatLieu(String maCL, String tenCL);

    public void importDataFromExcel(InputStream excelFile);
}
