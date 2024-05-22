package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.ChatLieu;
import com.example.shoesmanagement.model.Giay;
import com.example.shoesmanagement.model.Hang;
import com.example.shoesmanagement.service.ChatLieuService;
import com.example.shoesmanagement.service.GiayService;
import com.example.shoesmanagement.service.HangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ban-hang")
public class BanHangController {
    @Autowired
    private GiayService giayService;
    @Autowired
    private HangService hangService;

    @Autowired
    private ChatLieuService chatLieuService;

    @GetMapping("/hien-thi")
    public String banHang(Model model) {
        List<Giay> listGiay = giayService.getAllGiay();
        List<Hang> listHang = hangService.getALlHang();
        List<ChatLieu> listChatLieu = chatLieuService.getAllChatLieu();
        model.addAttribute("giay", listGiay);
        model.addAttribute("hang", listHang);
        model.addAttribute("chatLieu", listChatLieu);
        return "manage/ban-hang";
    }

}
