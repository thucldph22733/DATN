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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

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


    @RestController
    public class LoginApiController {

        @GetMapping("/api/check-login")
        public Map<String, Boolean> checkLoginStatus(HttpSession session) {
            KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
            Map<String, Boolean> status = new HashMap<>();
            status.put("loggedIn", khachHang != null);
            return status;
        }
    }


    @GetMapping("/cart")
    private String getShoppingCart(HttpSession session, Model model){
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");

        if(khachHang == null){
            model.addAttribute("error", "Bạn cần đăng nhập để xem giỏ hàng");
            return "redirect:/buyer/login"; // Sử dụng redirect ở đây
        }

        model.addAttribute("reLoadPageCart", true);
        showDataBuyer(model);

        return "/online/shopping-cart";
    }
    @PostMapping("/cart/updateQuantity")
    @ResponseBody
    public void updateQuantity(@RequestParam UUID idCTG, @RequestParam int quantity) {

        GioHang gioHang = (GioHang) session.getAttribute("GHLogged") ;
        ChiTietGiay chiTietGiay = gctService.getByIdChiTietGiay(idCTG);

        GioHangChiTiet gioHangChiTiet = ghctService.findByCTGActiveAndKhachHangAndTrangThai(chiTietGiay, gioHang);
        gioHangChiTiet.setSoLuong(quantity);
        gioHangChiTiet.setDonGia(quantity*chiTietGiay.getGiaBan());
        ghctService.addNewGHCT(gioHangChiTiet);

    }



//    @GetMapping("/cart/delete/{idCTG}")
//    private String deleteInCard(Model model, @PathVariable UUID idCTG, RedirectAttributes redirectAttribute){
//
//        ChiTietGiay chiTietGiay = gctService.getByIdChiTietGiay(idCTG);
//        GioHangChiTiet gioHangChiTiet = ghctService.findByCTSPActive(chiTietGiay);
//        gioHangChiTiet.setTrangThai(0);
//        ghctService.addNewGHCT(gioHangChiTiet);
//
//        showDataBuyer(model);
//        redirectAttribute.addFlashAttribute("successMessage", "Sản phẩm đã được xoá khỏi giỏ hàng thành công!");
//
//        return "redirect:/buyer/cart";
//    }

    @GetMapping("/cart/delete/{idCTG}")
    private String deleteInCard(Model model, @PathVariable UUID idCTG, RedirectAttributes redirectAttribute) {
        ChiTietGiay chiTietGiay = gctService.getByIdChiTietGiay(idCTG);
        GioHangChiTiet gioHangChiTiet = ghctService.findByCTSPActive(chiTietGiay);
        gioHangChiTiet.setTrangThai(0);
        ghctService.addNewGHCT(gioHangChiTiet);

        showDataBuyer(model);
        redirectAttribute.addFlashAttribute("successMessage", "Sản phẩm đã được xoá khỏi giỏ hàng thành công!");

        return "redirect:/buyer/cart";
    }



    //    private void showDataBuyer(Model model){
//        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
//        GioHang gioHang = (GioHang) session.getAttribute("GHLogged") ;
//        List<GioHangChiTiet> listGHCTActiveOriginal = ghctService.findByGHActive(gioHang);
//        // Khởi tạo một danh sách rỗng nếu kết quả là null để tránh NullPointerException
//
//        List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);
//        Integer sumProductInCart = listGHCTActive.size();
//
//        Double sumAllProduct = listGHCTActive.stream().mapToDouble(GioHangChiTiet::getDonGia).sum();
//
//        if (listGHCTActive != null){
//            for (GioHangChiTiet gioHangChiTiet: listGHCTActive) {
//                gioHangChiTiet.setDonGia(gioHangChiTiet.getChiTietGiay().getGiaBan()* gioHangChiTiet.getSoLuong());
//                gioHangChiTiet.setDonGiaTruocKhiGiam(gioHangChiTiet.getChiTietGiay().getSoTienTruocKhiGiam()* gioHangChiTiet.getSoLuong());
//                ghctService.addNewGHCT(gioHangChiTiet);
//            }
//        }
//        model.addAttribute("fullNameLogin", khachHang.getHoTenKH());
//        model.addAttribute("sumProductInCart", sumProductInCart);
//        model.addAttribute("listCartDetail", listGHCTActive);
//        model.addAttribute("totalPrice", sumAllProduct);
//    }
    private void showDataBuyer(Model model) {
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        GioHang gioHang = (GioHang) session.getAttribute("GHLogged");

        List<GioHangChiTiet> listGHCTActiveOriginal = ghctService.findByGHActive(gioHang);
        // Khởi tạo một danh sách rỗng nếu kết quả là null để tránh NullPointerException
        List<GioHangChiTiet> listGHCTActive = listGHCTActiveOriginal != null ? listGHCTActiveOriginal : new ArrayList<>();

        Integer sumProductInCart = listGHCTActive.size();
        Double sumAllProduct = listGHCTActive.stream().mapToDouble(GioHangChiTiet::getDonGia).sum();

        if (!listGHCTActive.isEmpty()) {
            for (GioHangChiTiet gioHangChiTiet : listGHCTActive) {
                gioHangChiTiet.setDonGia(gioHangChiTiet.getChiTietGiay().getGiaBan() * gioHangChiTiet.getSoLuong());
                ghctService.addNewGHCT(gioHangChiTiet);
            }
        }

        model.addAttribute("fullNameLogin", khachHang != null ? khachHang.getHoTenKH() : "");
        model.addAttribute("sumProductInCart", sumProductInCart);
        model.addAttribute("listCartDetail", listGHCTActive);
        model.addAttribute("totalPrice", sumAllProduct);
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


}
