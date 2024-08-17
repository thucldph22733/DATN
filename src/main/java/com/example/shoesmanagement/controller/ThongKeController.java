
package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.HoaDonChiTiet;
import com.example.shoesmanagement.repository.*;
import com.example.shoesmanagement.viewModel.CTHDViewModel;
import com.example.shoesmanagement.viewModel.HieuSuatBanHang;
import com.example.shoesmanagement.viewModel.SanPhamSapHet;
import com.example.shoesmanagement.viewModel.Top5SPBanChayTrongThang;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.NumberFormat;

import java.time.LocalDate;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller

@RequestMapping("/thong-ke")

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
            , @ModelAttribute("error") String error, @ModelAttribute("Errormessage") String Errormessage,
                          @RequestParam(value = "tuNgay", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
                          @RequestParam(value = "denNgay", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay) {
        model.addAttribute("tong", khachHangRepository.getTongKH());
        model.addAttribute("tonggiay", giayChiTietRepository.getTongGiay());

        //làm tròn doanh thu


        Optional<Double> ltn = hoaDonChiTietRepository.getLaiThangNay();

        Optional<Double> tlbr = hoaDonChiTietRepository.getTongTienLaiCuaHang2();
        Optional<Integer> tongSPBanTrongNgay = hoaDonChiTietRepository.getTongSPBanTrongNgay();
        Optional<Integer> tongSPBanTrongThang = hoaDonChiTietRepository.getTongSPBanTrongThang();
        Optional<Double> dto = hoaDonChiTietRepository.getDoanhThuOnline();
        Optional<Double> dtf = hoaDonChiTietRepository.getDoanhThuTaiQuay();


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

        if (dto.isPresent()) {
            Double c = dto.get();
            String formatdtt = currencyFormat.format(c);
            model.addAttribute("dto", formatdtt);
        } else {
            model.addAttribute("dto", "0đ");
        }

        if (dtf.isPresent()) {
            Double g = dtf.get();
            String formatdtt = currencyFormat.format(g);
            model.addAttribute("dtf", formatdtt);
        } else {
            model.addAttribute("dtf", "0đ");
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

        // biểu đồ thống kê

        // Lấy dữ liệu thống kê theo ngày
        if (tuNgay != null && denNgay != null) {
            Date startDate = Date.valueOf(tuNgay);
            Date endDate = Date.valueOf(denNgay);
            Integer soLuongTheoNgay = hoaDonChiTietRepository.getSoLuongTheoNgay(startDate, endDate);
            model.addAttribute("soLuongTheoNgay", soLuongTheoNgay != null ? soLuongTheoNgay : 0);
        }
        //theo tháng
        List<String> listThem= Arrays.asList("Tháng 1","Tháng 2","Tháng 3","Tháng 4","Tháng 5",
                "Tháng 6","Tháng 7","Tháng 8","Tháng 9","Tháng 10","Tháng 11","Tháng 12");
        List<String> listThang = new ArrayList<>(listThem);

        List<Integer> list = Arrays.asList(hoaDonChiTietRepository.getThang1(),
                hoaDonChiTietRepository.getThang2(),
                hoaDonChiTietRepository.getThang3(),
                hoaDonChiTietRepository.getThang4(),
                hoaDonChiTietRepository.getThang5(),
                hoaDonChiTietRepository.getThang6(),
                hoaDonChiTietRepository.getThang7(),
                hoaDonChiTietRepository.getThang8(),
                hoaDonChiTietRepository.getThang9(),
                hoaDonChiTietRepository.getThang10(),
                hoaDonChiTietRepository.getThang11(),
                hoaDonChiTietRepository.getThang12()

        );
        List<Integer> listDoanhSo = new ArrayList<>(list);
        model.addAttribute("listThang", listThang);
        model.addAttribute("listDoanhSo", listDoanhSo);
        // theo ngày
        List<String> listThemNgay = new ArrayList<>();
        listThemNgay.add("Ngày Hôm Nay");
        List<Integer> doanhSoNgay = new ArrayList<>();
        doanhSoNgay.add(hoaDonChiTietRepository.getNgaythu1());
        model.addAttribute("Ngay", listThemNgay);
        model.addAttribute("listSL", doanhSoNgay);
        //theo năm
        List<String> listThemNam = new ArrayList<>();
        listThemNam.add("2022");
        listThemNam.add("2023");
        listThemNam.add("2024");

        List<Integer> doanhSoNam = new ArrayList<>();
        doanhSoNam.add(hoaDonChiTietRepository.Nam2022());
        doanhSoNam.add(hoaDonChiTietRepository.Nam2023());
        doanhSoNam.add(hoaDonChiTietRepository.Nam2024());
        model.addAttribute("Nam", listThemNam);
        model.addAttribute("listNam1", doanhSoNam);

        return "thongke/thong-ke";


    }

    @GetMapping("/api/thong-ke")
    public ResponseEntity<Map<String, Object>> getThongKe(
            @RequestParam(value = "tuNgay", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime tuNgay,
            @RequestParam(value = "denNgay", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime denNgay) {

        Double totalRevenue;
        Double totalRevenue1;
        Double totalRevenue2;


        if (tuNgay != null && denNgay != null) {
            Timestamp startDate = Timestamp.valueOf(tuNgay);
            Timestamp endDate = Timestamp.valueOf(denNgay);
            Optional<Double> tlbr = hoaDonChiTietRepository.getTongTienLaiCuaHang(startDate, endDate);
            Optional<Double> dto = hoaDonChiTietRepository.getDoanhThuOnline2(startDate, endDate);
            Optional<Double> dtf = hoaDonChiTietRepository.getDoanhThuTaiQuay2(startDate, endDate);
            totalRevenue = tlbr.orElse(0.0);
            totalRevenue1 = dto.orElse(0.0);
            totalRevenue2 = dtf.orElse(0.0);
        } else {
            Optional<Double> tlbr = hoaDonChiTietRepository.getTongTienLaiCuaHang2();
            totalRevenue = tlbr.orElse(0.0);

            Optional<Double> dto = hoaDonChiTietRepository.getDoanhThuOnline();
            totalRevenue1 = dto.orElse(0.0);

            Optional<Double> dtf = hoaDonChiTietRepository.getDoanhThuTaiQuay();
            totalRevenue2 = dtf.orElse(0.0);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("tlbr", totalRevenue);
        response.put("dto", totalRevenue1);
        response.put("dtf", totalRevenue2);

        return ResponseEntity.ok(response);
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



        return "manage/thong-ke/detailCTSPNV";


    }
}
