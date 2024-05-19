package com.example.shoesmanagement.buyerController;

import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.GioHangChiTiet;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.service.CTGViewModelService;
import com.example.shoesmanagement.service.GHCTService;
import com.example.shoesmanagement.service.GiayChiTietService;
import com.example.shoesmanagement.viewModel.CTGViewModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class HomeController {
    @Autowired
    private CTGViewModelService ctgViewModelService;

    @Autowired
    private HttpSession session;

    @Autowired
    private GHCTService ghctService;


    @RequestMapping(value = {"", "/", "/home"})
    public String hone(Model model){

        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null){
            String fullName = khachHang.getHoTenKH();
            model.addAttribute("fullNameLogin", fullName);
            GioHang gioHang = (GioHang) session.getAttribute("GHLogged") ;

            List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);


            Integer sumProductInCart = listGHCTActive.size();
            model.addAttribute("sumProductInCart", sumProductInCart);
            model.addAttribute("heartLogged", true);

        }else {
            model.addAttribute("messageLoginOrSignin", true);
        }

        List<CTGViewModel> listCTGModelNew = ctgViewModelService.getAllOrderTgNhap();
        List<CTGViewModel> listCTGModelBestSeller = ctgViewModelService.getAllOrderBestSeller();
        List<CTGViewModel> top12CTGModelNew = listCTGModelNew.subList(0, Math.min(listCTGModelNew.size(), 12));
        List<CTGViewModel> top12CTGModelBestSeller = listCTGModelBestSeller.subList(0, Math.min(listCTGModelBestSeller.size(), 12));
        model.addAttribute("listCTGModelNew",top12CTGModelNew);
        model.addAttribute("listCTGModelBestSeller",top12CTGModelBestSeller);
        return "online/index";
    }
}
