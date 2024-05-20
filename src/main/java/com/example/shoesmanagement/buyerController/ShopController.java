package com.example.shoesmanagement.buyerController;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.service.*;
import com.example.shoesmanagement.viewModel.CTGViewModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/buyer")
public class ShopController {

    @Autowired
    private HangService hangService;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private MauSacService mauSacService;

    @Autowired
    private CTGViewModelService ctgViewModelService;

    @Autowired
    private GHCTService ghctService;

    @Autowired
    private HttpSession session;


//    @Autowired
//    private HttpServletRequest request;
@GetMapping("/shop")
private String getShopBuyer(Model model,
                            @RequestParam(name = "pageSize", defaultValue = "9") Integer pageSize,
                            @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
    showDataBuyerShop(model);
    checkKhachHangLogged(model);

    List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();

    Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
    Page<CTGViewModel> page = ctgViewModelService.getAllPage(pageable);

    model.addAttribute("totalPage", page.getTotalPages());
    model.addAttribute("listCTGModel", page.getContent());
    model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
    model.addAttribute("pageNumber", true);
    model.addAttribute("showPage", true);
    return "online/shop";
}
    @GetMapping("/shop/best")
    private String getShopBuyerBestSeller(Model model){

        checkKhachHangLogged(model);

        List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();
        List<CTGViewModel> listCTGModelBestSeller = ctgViewModelService.getAllOrderBestSeller();

        model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
        model.addAttribute("pageNumber", true);
        model.addAttribute("listCTGModel", listCTGModelBestSeller);

        showDataBuyerShop(model);
        return "online/shop";
    }

    @GetMapping("/shop/new")
    private String getShopBuyerNew(Model model){

        checkKhachHangLogged(model);

        List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();
        List<CTGViewModel> listCTGModelNew = ctgViewModelService.getAllOrderTgNhap();

        model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
        model.addAttribute("pageNumber", true);
        model.addAttribute("listCTGModel", listCTGModelNew);

        showDataBuyerShop(model);
        return "online/shop";
    }
    @GetMapping("/shop/sale")
    private String getShopBuyerSale(Model model){

        checkKhachHangLogged(model);

        List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();
        List<CTGViewModel> listCTGModelNew = ctgViewModelService.getAllOrderTgNhap();

        List<CTGViewModel> ctgViewModelList = new ArrayList<>();

        model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
        model.addAttribute("pageNumber", true);
        model.addAttribute("listCTGModel", ctgViewModelList);

        showDataBuyerShop(model);
        return "online/shop";
    }

    @GetMapping("/shop/brand/{idHang}")
    private String getShopBrandBuyer(Model model,@PathVariable UUID idHang){
        showDataBuyerShop(model);
        checkKhachHangLogged(model);

        List<CTGViewModel> listCTGByBrand = ctgViewModelService.findByIDHang(idHang);
        int sumProductByHang = listCTGByBrand.size();
        model.addAttribute("sumProduct", sumProductByHang);

        model.addAttribute("listCTGModel", listCTGByBrand);

        List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();
        model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
        return "online/shop";
    }

    @GetMapping("/shop/price/type=1")
    private String getShopPriceType1(Model model){

        checkKhachHangLogged(model);

        List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();
        List<CTGViewModel> listCTGModelNew = ctgViewModelService.getAllOrderTgNhap();

        List<CTGViewModel> ctgViewModelList = new ArrayList<>();

        for (CTGViewModel xx:listCTGModelNew) {
            if (xx.getMinPrice() < 2000000.0){
                ctgViewModelList.add(xx);
            }
        }

        model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
        model.addAttribute("pageNumber", true);
        model.addAttribute("listCTGModel", ctgViewModelList);

        showDataBuyerShop(model);
        return "online/shop";
    }

    @GetMapping("/shop/price/type=2")
    private String getShopPriceType2(Model model){

        checkKhachHangLogged(model);

        List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();
        List<CTGViewModel> listCTGModelNew = ctgViewModelService.getAllOrderTgNhap();

        List<CTGViewModel> ctgViewModelList = new ArrayList<>();

        for (CTGViewModel xx:listCTGModelNew) {
            if (xx.getMinPrice() >= 2000000.0 && xx.getMinPrice() <= 3000000.0){
                ctgViewModelList.add(xx);
            }
        }

        model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
        model.addAttribute("pageNumber", true);
        model.addAttribute("listCTGModel", ctgViewModelList);

        showDataBuyerShop(model);
        return "online/shop";
    }
    @GetMapping("/shop/price/type=3")
    private String getShopPriceType3(Model model){

        checkKhachHangLogged(model);

        List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();
        List<CTGViewModel> listCTGModelNew = ctgViewModelService.getAllOrderTgNhap();

        List<CTGViewModel> ctgViewModelList = new ArrayList<>();

        for (CTGViewModel xx:listCTGModelNew) {
            if (xx.getMinPrice() >= 3000000.0 && xx.getMinPrice() <= 5000000.0){
                ctgViewModelList.add(xx);
            }
        }

        model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
        model.addAttribute("pageNumber", true);
        model.addAttribute("listCTGModel", ctgViewModelList);

        showDataBuyerShop(model);
        return "online/shop";
    }

    @GetMapping("/shop/price/type=4")
    private String getShopPriceType4(Model model){

        checkKhachHangLogged(model);

        List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();
        List<CTGViewModel> listCTGModelNew = ctgViewModelService.getAllOrderTgNhap();

        List<CTGViewModel> ctgViewModelList = new ArrayList<>();

        for (CTGViewModel xx:listCTGModelNew) {
            if (xx.getMinPrice() >= 5000000.0 && xx.getMinPrice() <= 8000000.0){
                ctgViewModelList.add(xx);
            }
        }

        model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
        model.addAttribute("pageNumber", true);
        model.addAttribute("listCTGModel", ctgViewModelList);

        showDataBuyerShop(model);
        return "online/shop";
    }

    @GetMapping("/shop/price/type=5")
    private String getShopPriceType5(Model model){

        checkKhachHangLogged(model);

        List<CTGViewModel> listCTGModelSoldOff = ctgViewModelService.getAllSoldOff();
        List<CTGViewModel> listCTGModelNew = ctgViewModelService.getAllOrderTgNhap();

        List<CTGViewModel> ctgViewModelList = new ArrayList<>();

        for (CTGViewModel xx:listCTGModelNew) {
            if (xx.getMinPrice() >= 8000000.0){
                ctgViewModelList.add(xx);
            }
        }

        model.addAttribute("listCTGModelSoldOff", listCTGModelSoldOff);
        model.addAttribute("pageNumber", true);
        model.addAttribute("listCTGModel", ctgViewModelList);

        showDataBuyerShop(model);
        return "online/shop";
    }
    private void showDataBuyerShop(Model model){

        List<Hang> listHang = hangService.getAllActive();
        model.addAttribute("listBrand", listHang);

        List<Size> listSize = sizeService.getAllSizeActiveOrderByName();
        model.addAttribute("listSize", listSize);

        List<MauSac> listColor = mauSacService.getMauSacActive();
        model.addAttribute("listColor", listColor);

        List<CTGViewModel> listCTGModelNew = ctgViewModelService.getAllOrderTgNhap();
        model.addAttribute("sumProduct", listCTGModelNew.size());

        List<CTGViewModel> ctgViewModelList = new ArrayList<>();

    }

    private void checkKhachHangLogged(Model model){
        KhachHang khachHang = (KhachHang) session.getAttribute("KhachHangLogin");
        if (khachHang != null){
            String fullName = khachHang.getHoTenKH();
            model.addAttribute("fullNameLogin", fullName);

            GioHang gioHang = (GioHang) session.getAttribute("GHLogged") ;

            List<GioHangChiTiet> listGHCTActive = ghctService.findByGHActive(gioHang);

            Integer sumProductInCart = listGHCTActive.size();
            model.addAttribute("heartLogged", true);
            model.addAttribute("sumProductInCart", sumProductInCart);

        }else {
            model.addAttribute("messageLoginOrSignin", true);
        }
    }
    }

