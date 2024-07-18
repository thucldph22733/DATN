package com.example.shoesmanagement.shipperController;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class HomeOrder {
    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private GiaoHangService giaoHangService;

    @Autowired
    private ViTriDonHangService viTriDonHangServices;

    @Autowired
    private GiayChiTietService giayChiTietService;

    @Autowired
    private LSThanhToanService lsThanhToanService;

    @RequestMapping(value = {"", "/", "/home"})
    private String getHomeShipping(Model model) {
        if (session.getAttribute("shipperLogged") == null) {
            // Nếu managerLogged bằng null, quay về trang login
            return "redirect:/login";
        }
        showData(model);
//
        showDataTab1(model);
        return "transportation/index";
    }

    @GetMapping("/delivery/update/{idHD}")
    private String viewUpdateGiaoHang(Model model, @PathVariable UUID idHD) {

        showData(model);

        HoaDon hoaDon = hoaDonService.getOne(idHD);
        GiaoHang giaoHang = hoaDon.getGiaoHang();
        List<ViTriDonHang> giaoHangList = viTriDonHangServices.findByGiaoHang(giaoHang);
        int soLanHuy = 0;
        for (ViTriDonHang xx : giaoHangList) {
            if (xx.getTrangThai() == 2) {
                soLanHuy++;
            }
        }

        showDataGH(model, hoaDon);
        showDataTab2(model);
        model.addAttribute("soLanHuy", soLanHuy);
        model.addAttribute("giaoHangList", giaoHangList);
        return "transportation/index";

    }

    @PostMapping("/delivery/update/{idHD}")
    private String updateGiaoHang(Model model, @PathVariable UUID idHD) {
        NhanVien nhanVien = (NhanVien) session.getAttribute("shipperLogged");
        HoaDon hoaDon = hoaDonService.getOne(idHD);
        model.addAttribute("nameNhanVien", nhanVien.getHoTenNV());
        showData(model);

        String trangThaiGiaoHang = request.getParameter("trangThaiGiaoHang");

        String thanhPho = request.getParameter("city");
        String district = request.getParameter("district");
        String ward = request.getParameter("ward");
        String moTa = request.getParameter("moTa");

        GiaoHang giaoHang = hoaDon.getGiaoHang();
        List<ViTriDonHang> giaoHangList = viTriDonHangServices.findByGiaoHang(giaoHang);

        int soLanHuy = 0;

        for (ViTriDonHang xx : giaoHangList) {
            if (xx.getTrangThai() == 2) {
                soLanHuy++;
            }
        }
        Date date = new Date();
        Integer sequenceNumber = (Integer) session.getAttribute("sequenceNumber");
        if (sequenceNumber == null) {
            sequenceNumber = 1; // Khởi tạo nếu chưa có trong session
        }

        // Tạo mã hóa đơn với ngày hôm nay và số thứ tự
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");
        String strDate = formatter.format(date);


        if (trangThaiGiaoHang.equals("daGuiHang")) {

            String donViVanChuyen = request.getParameter("donViVanChuyen");

            giaoHang.setTenDVVC("Human Express");
            giaoHangService.saveGiaoHang(giaoHang);

            if (!donViVanChuyen.equals("humanExpress")) {
                String maVanDonGH = "MVD_" + strDate + "_" + sequenceNumber;

                // Tăng số thứ tự và lưu lại trong session
                sequenceNumber++;
                session.setAttribute("sequenceNumber", sequenceNumber);
                if (donViVanChuyen.equals("ghn")) {
                    giaoHang.setTenDVVC("Giao hàng nhanh");
                    giaoHang.setMaVanDon(maVanDonGH);
                }
                if (donViVanChuyen.equals("ghtk")) {
                    giaoHang.setTenDVVC("Giao Hàng Tiết Kiệm");
                    giaoHang.setMaVanDon(maVanDonGH);
                }
                if (donViVanChuyen.equals("viettelPost")) {
                    giaoHang.setTenDVVC("Viettel Post");
                    giaoHang.setMaVanDon(maVanDonGH);
                }
                if (donViVanChuyen.equals("ahamove")) {
                    giaoHang.setTenDVVC("Ahamove");
                    giaoHang.setMaVanDon(maVanDonGH);
                }
                if (donViVanChuyen.equals("grab")) {
                    giaoHang.setTenDVVC("Grab");
                    giaoHang.setMaVanDon(maVanDonGH);
                }
                if (donViVanChuyen.equals("be")) {
                    giaoHang.setTenDVVC("Be");
                    giaoHang.setMaVanDon(maVanDonGH);
                }
                giaoHangService.saveGiaoHang(giaoHang);
            }
            ViTriDonHang viTriDonHang = new ViTriDonHang();


            viTriDonHang.setViTri("Đã lấy/gửi hành thành công ");
            viTriDonHang.setTrangThai(1);
            viTriDonHang.setGiaoHang(hoaDon.getGiaoHang());
            viTriDonHang.setThoiGian(new Date());
            viTriDonHang.setNoiDung(moTa);
            viTriDonHang.setThoiGian(date);
            viTriDonHangServices.addViTriDonHang(viTriDonHang);
            hoaDon.setTrangThai(3);
            giaoHang.setTgThanhToan(date);
            giaoHang.setTgNhan(date);
            giaoHang.setNoiDung("Chờ giao hàng");
            hoaDonService.add(hoaDon);
            showData(model);
            showDataTab2(model);
            model.addAttribute("messageLay", "Lấy hàng thành công.");
            return "transportation/index";
        } else if (trangThaiGiaoHang.equals("thanhCong")) {
            String viTri = "Đơn hàng đã giao hàng thành công";


            ViTriDonHang viTriDonHang = new ViTriDonHang();

            viTriDonHang.setViTri(viTri);
            viTriDonHang.setThoiGian(date);
            viTriDonHang.setTrangThai(1);
            viTriDonHang.setNoiDung(moTa);
            viTriDonHang.setGiaoHang(hoaDon.getGiaoHang());
            viTriDonHangServices.addViTriDonHang(viTriDonHang);

            hoaDon.setTrangThai(4);
            giaoHang.setTgThanhToan(date);
            hoaDon.setTgThanhToan(new Date());
            hoaDonService.add(hoaDon);
            giaoHang.setNoiDung("Giao thành công");
            LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
            lichSuThanhToan.setHoaDon(hoaDon);
            lichSuThanhToan.setTgThanhToan(date);
            lichSuThanhToan.setSoTienThanhToan(0.0);
            lichSuThanhToan.setKhachHang(hoaDon.getKhachHang());
            lichSuThanhToan.setMaLSTT("LSST0" + date.getTime());
            lichSuThanhToan.setNoiDungThanhToan("Khách hàng đã thanh toán cho đơn hàng");
            lichSuThanhToan.setTrangThai(1);
            lsThanhToanService.addLSTT(lichSuThanhToan);


            showData(model);
            showDataTab2(model);
            model.addAttribute("messageGiao", "Giao hàng thành công.");
            return "transportation/index";
        } else if (trangThaiGiaoHang.equals("thatBai")) {

            String viTri = "Đơn hàng giao thất bại ( " + moTa + " )";
            ViTriDonHang viTriDonHang = new ViTriDonHang();

            viTriDonHang.setViTri(viTri);
            viTriDonHang.setThoiGian(new Date());
            viTriDonHang.setTrangThai(2);
            viTriDonHang.setNoiDung(moTa);
            viTriDonHang.setGiaoHang(giaoHang);
            viTriDonHangServices.addViTriDonHang(viTriDonHang);

            if (soLanHuy == 1) {
                hoaDon.setTrangThai(5);
                giaoHang.setTgHuy(new Date());
                giaoHang.setLyDoHuy(moTa);
                giaoHang.setTrangThai(2);
                hoaDon.setTgHuy(new Date());
                hoaDonService.add(hoaDon);
                giaoHang.setNoiDung("Giao thất bại");


                ViTriDonHang viTriDonHang2 = new ViTriDonHang();
                viTriDonHang.setViTri("Đơn hàng đã bị hủy");
                viTriDonHang.setThoiGian(new Date());
                viTriDonHang.setTrangThai(2);
                viTriDonHang.setNoiDung(moTa);
                viTriDonHang.setGiaoHang(giaoHang);
                viTriDonHangServices.addViTriDonHang(viTriDonHang2);

                LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
                lichSuThanhToan.setHoaDon(hoaDon);
                lichSuThanhToan.setTgThanhToan(date);
                lichSuThanhToan.setSoTienThanhToan(hoaDon.getTongTien());
                lichSuThanhToan.setKhachHang(hoaDon.getKhachHang());
                lichSuThanhToan.setMaLSTT("LSST0" + date.getTime());
                lichSuThanhToan.setNoiDungThanhToan("Khách hàng không nhận hàng");
                lichSuThanhToan.setTrangThai(5);
                lsThanhToanService.addLSTT(lichSuThanhToan);

                showDataTab2(model);
                showData(model);
                model.addAttribute("messageHuy", "Giao hàng thất bại.");

                return "transportation/index";
            }
            showData(model);
            showDataTab2(model);
            return "transportation/index";
        } else {
            String viTri = "Đơn hàng đã đến" + ", " + ward + ", " + district + " , " + thanhPho;
            ViTriDonHang viTriDonHang = new ViTriDonHang();

            viTriDonHang.setViTri(viTri);
            viTriDonHang.setThoiGian(new Date());
            viTriDonHang.setTrangThai(1);
            viTriDonHang.setNoiDung(moTa);
            viTriDonHang.setGiaoHang(giaoHang);
            viTriDonHangServices.addViTriDonHang(viTriDonHang);

            for (ViTriDonHang x : giaoHangList) {
                if (x.getTrangThai() == 1) {
                    x.setTrangThai(0);
                    viTriDonHangServices.addViTriDonHang(x);
                }
            }
            showData(model);
            showDataTab2(model);
            return "transportation/index";

        }
    }

    @GetMapping("/delivery/refund/update/{idHD}")
    private String viewUpdateRefundGiaoHang(Model model, @PathVariable UUID idHD) {
        showData(model);
        HoaDon hoaDon = hoaDonService.getOne(idHD);
        showDataTab2(model);

        return "transportation/index";

    }




    private void showDataGH(Model model, HoaDon hoaDon) {
        GiaoHang giaoHangListActive = hoaDon.getGiaoHang();
        model.addAttribute("showHTGH", "true");
        model.addAttribute("HoaDonVanChuyen", hoaDon);
        model.addAttribute("giaoHangListActive", giaoHangListActive);
    }

    private void showData(Model model) {
        NhanVien nhanVien = (NhanVien) session.getAttribute("shipperLogged");
        List<HoaDon> test = hoaDonService.listHoaDonOnline();
//        List<HoaDon> allHoaDonList = hoaDonService.listAllHoaDonByNhanVien(nhanVien);
//        List<HoaDon> hoaDonDGList = hoaDonService.listHoaDonByNhanVienAndTrangThai(nhanVien, 2);
//        List<HoaDon> hoaDonDoneList = hoaDonService.listHoaDonHuyAndThanhCongByNhanVien(nhanVien);

        long sumDangGiao = test.stream()
                .filter(bill -> bill.getTrangThai() == 3)
                .count();
        long sumChoLayHang = test.stream()
                .filter(bill -> bill.getTrangThai() == 2)
                .count();
        long sumGiaoTc = test.stream()
                .filter(bill -> bill.getTrangThai() == 4)
                .count();
        long sumDaHuy = test.stream()
                .filter(bill -> bill.getTrangThai() == 5)
                .count();
        long sumDHMuaHang = test.stream()
                .filter(bill -> bill.getTrangThai() == 2 || bill.getTrangThai() == 3 || bill.getTrangThai() == 4 || bill.getTrangThai() == 5)
                .count();
        model.addAttribute("allHoaDonList", test);

        model.addAttribute("dangGiao", sumDangGiao);
        model.addAttribute("choLayHang", sumChoLayHang);
        model.addAttribute("sumDHMuaHang", sumDHMuaHang);
        model.addAttribute("giaoThanhCong", sumGiaoTc);
        model.addAttribute("huy", sumDaHuy);

        model.addAttribute("hoaDonDGList", test);
        model.addAttribute("hoaDonDoneList", test);
        model.addAttribute("nameNhanVien", nhanVien.getHoTenNV());
    }

    private void showDataTab1(Model model) {
        model.addAttribute("activeAll", "nav-link active");
        model.addAttribute("activeVanChuyen", "nav-link");
        model.addAttribute("activeDone", "nav-link");
        model.addAttribute("tabpane1", "tab-pane show active");
        model.addAttribute("tabpane2", "tab-pane");
        model.addAttribute("tabpane3", "tab-pane");
    }

    private void showDataTab2(Model model) {
        model.addAttribute("activeAll", "nav-link");
        model.addAttribute("activeVanChuyen", "nav-link active");
        model.addAttribute("activeDone", "nav-link");
        model.addAttribute("tabpane1", "tab-pane");
        model.addAttribute("tabpane2", "tab-pane show active");
        model.addAttribute("tabpane3", "tab-pane");
    }

    private void showDataTab3(Model model) {
        model.addAttribute("activeAll", "nav-link");
        model.addAttribute("activeVanChuyen", "nav-link");
        model.addAttribute("activeDone", "nav-link active");
        model.addAttribute("tabpane1", "tab-pane");
        model.addAttribute("tabpane2", "tab-pane");
        model.addAttribute("tabpane3", "tab-pane show active");
    }
}
