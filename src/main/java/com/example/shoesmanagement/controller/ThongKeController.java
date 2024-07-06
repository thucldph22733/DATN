package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.HoaDonChiTiet;
import com.example.shoesmanagement.repository.*;
import com.example.shoesmanagement.viewModel.CTHDViewModel;
import com.example.shoesmanagement.viewModel.HieuSuatBanHang;
import com.example.shoesmanagement.viewModel.SanPhamSapHet;
import com.example.shoesmanagement.viewModel.Top5SPBanChayTrongThang;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ThongKe")
public class    ThongKeController {
    @Autowired
    private HttpSession session;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private GiayChiTietRepository giayChiTietRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private HinhAnhRepository hinhAnhRepository;
    @Autowired
    private GiayRepository giayRepository;
    @Autowired
    private CTGViewModelRepository ctgViewModelRepository;
    public NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    @GetMapping("/thong-ke")
    public String hienThi(Model model, @ModelAttribute("message") String message
            , @ModelAttribute("error") String error, @ModelAttribute("Errormessage") String Errormessage) {
        model.addAttribute("tong", khachHangRepository.getTongKH());
        model.addAttribute("tonggiay", giayChiTietRepository.getTongGiay());

        //làm tròn doanh thu


        Optional<Double> ltn = hoaDonChiTietRepository.getLaiThangNay();

        Optional<Double> tlbr = hoaDonChiTietRepository.getTongTienLaiCuaHang();
        Optional<Integer> tongSPBanTrongNgay = hoaDonChiTietRepository.getTongSPBanTrongNgay();
        Optional<Integer> tongSPBanTrongThang = hoaDonChiTietRepository.getTongSPBanTrongThang();


        if (tongSPBanTrongNgay.isPresent()) {
            Integer a = tongSPBanTrongNgay.get();

            model.addAttribute("sptn", a);
        } else {
            model.addAttribute("sptn", "0");
        }

        if (ltn.isPresent()) {
            Double b = ltn.get();
            String formatdtt = currencyFormat.format(b);
            model.addAttribute("ltn", formatdtt);
        } else {
            model.addAttribute("ltn", "0đ");
        }
        //Tổng tiền của cửa hàng
        if (tlbr.isPresent()) {
            Double c = tlbr.get();
            String formatdtt2 = currencyFormat.format(c);
            model.addAttribute("tlbr", formatdtt2);
        } else {
            model.addAttribute("tlbr", "0đ");
        }

        if (tongSPBanTrongThang.isPresent()) {
            Integer d = tongSPBanTrongThang.get();

            model.addAttribute("sptt", d);
        } else {
            model.addAttribute("sptt", "0");
        }
        List<Object[]> listCTGModelBestSeller = ctgViewModelRepository.getTop5SPBanChayTrongThang();
        System.out.println(listCTGModelBestSeller); // In ra để kiểm tra

        List<Top5SPBanChayTrongThang> dataTop5 = listCTGModelBestSeller.stream()
                .map(result -> {
                    System.out.println(Arrays.toString(result)); // In ra để kiểm tra từng mảng Object
                    return new Top5SPBanChayTrongThang(
                            (String) result[0],
                            (String) result[1],
                            (Integer) result[2]
                    );
                })
                .collect(Collectors.toList());

        model.addAttribute("spBanChay", dataTop5);
        System.out.println(model.getAttribute("spBanChay")); // In ra để kiểm tra
        return "ThongKe/thong-ke";

    }




    @GetMapping("/thongke/{maNV}")
    public String getEmployeeDetail(@PathVariable("maNV") String maNV, Model model) {
        // Xử lý dữ liệu chi tiết nhân viên dựa trên maNv
        System.out.println("Đã sang tc");
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDonChiTietRepository.getChiTietSPNhanVienBan(maNV);
        model.addAttribute("giay",giayRepository.findAll());
        model.addAttribute("chiTietGiay",giayChiTietRepository.findAll());
        model.addAttribute("hinhAnh",hinhAnhRepository.findAll());
        model.addAttribute("hoaDon",hoaDonRepository.findAll());
        model.addAttribute("ctspNV",hoaDonChiTiets);


        return "manage/ThongKe/detailCTSPNV";

    }
}
