package com.example.shoesmanagement.service.impl;



import com.example.shoesmanagement.model.ChatLieu;
import com.example.shoesmanagement.repository.ChatLieuRepository;
import com.example.shoesmanagement.service.ChatLieuService;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ChatLieuServiceImpl implements ChatLieuService {
    @Autowired
    private ChatLieuRepository chatLieuRepository;

    @Override
    public List<ChatLieu> getAllChatLieu() {
        return chatLieuRepository.findAllByOrderByTgThemDesc();
    }

    @Override
    public void save(ChatLieu chatLieu) {
        chatLieuRepository.save(chatLieu);
    }

    @Override
    public void deleteByIdChatLieu(UUID id) {
        chatLieuRepository.deleteById(id);
    }

    @Override
    public ChatLieu getByIdChatLieu(UUID id) {
        return chatLieuRepository.findById(id).orElse(null);
    }

    @Override
    public List<ChatLieu> fillterChatLieu(String maCL, String tenCL) {
        if ("Mã Chất Liệu".equals(maCL) && "Tên Chất Liệu".equals(tenCL)) {
            return chatLieuRepository.findAll();
        }
        return chatLieuRepository.findByMaChatLieuOrTenChatLieu(maCL, tenCL);
    }

    @Override
    public void importDataFromExcel(InputStream excelFile) {
        try (Workbook workbook = new XSSFWorkbook(excelFile)) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên (index 0)

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    // Bỏ qua hàng đầu tiên nếu nó là tiêu đề
                    continue;
                }
                ChatLieu chatLieu = new ChatLieu();
                chatLieu.setMaChatLieu(row.getCell(0).getStringCellValue()); // Cột 0 trong tệp Excel
                chatLieu.setTenChatLieu(row.getCell(1).getStringCellValue()); // Cột 0 trong tệp Excel
                chatLieu.setTgThem(new Date());
                chatLieu.setTrangThai(1);
                chatLieuRepository.save(chatLieu);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi nếu cần
        }
    }
}
