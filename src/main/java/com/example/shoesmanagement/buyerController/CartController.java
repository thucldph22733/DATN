package com.example.shoesmanagement.buyerController;


import com.example.shoesmanagement.model.ChiTietGiay;
import com.example.shoesmanagement.model.GioHang;
import com.example.shoesmanagement.model.GioHangChiTiet;
import com.example.shoesmanagement.model.KhachHang;
import com.example.shoesmanagement.service.GHCTService;
import com.example.shoesmanagement.service.GiayChiTietService;
import com.example.shoesmanagement.service.GiayService;
import com.example.shoesmanagement.service.MauSacService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Controller
@RequestMapping("/buyer")
public class CartController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;

    @Autowired
    private GHCTService ghctService;

    @Autowired
    private GiayChiTietService gctService;


    @Autowired
    private MauSacService mauSacService;

    @Autowired
    private GiayService giayService;

    @GetMapping("/cart")
    private String getShoppingCart(Model model){
        model.addAttribute("reLoadPageCart", true);
        showDataBuyer(model);

        return "/online/shopping-cart";
    }

    @GetMapping("/cart/delete/{idCTG}")
    private String deleteInCard(Model model, @PathVariable UUID idCTG, RedirectAttributes redirectAttribute){

        ChiTietGiay chiTietGiay = gctService.getByIdChiTietGiay(idCTG);
        GioHangChiTiet gioHangChiTiet = ghctService.findByCTSPActive(chiTietGiay);
        gioHangChiTiet.setTrangThai(0);
        ghctService.addNewGHCT(gioHangChiTiet);

        showDataBuyer(model);
        redirectAttribute.addFlashAttribute("successMessage", "Sản phẩm đã được xoá khỏi giỏ hàng thành công!");

        return "redirect:/buyer/cart";
    }
    private void showDataBuyer(Model model){
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged") ;

        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
        Integer sumProductInCart = listGHCTActive.size();

        if (listGHCTActive != null){
            for (GioHangChiTiet gioHangChiTiet: listGHCTActive) {
                gioHangChiTiet.setDonGia(gioHangChiTiet.getChiTietGiay().getGiaBan()* gioHangChiTiet.getSoLuong());
                gioHangChiTiet.setDonGiaTruocKhiGiam(gioHangChiTiet.getChiTietGiay().getSoTienTruocKhiGiam()* gioHangChiTiet.getSoLuong());
                ghctService.addNewGHCT(gioHangChiTiet);
            }
        }
        model.addAttribute("fullNameLogin", khachHang.getHoTenKH());
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("listCartDetail", listGHCTActive);
    }
    public String generateRandomNumbers() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomNumber = random.nextInt(10); // Tạo số ngẫu nhiên từ 0 đến 9
            sb.append(randomNumber);
        }
        return sb.toString();
    }

//aaaa
}
