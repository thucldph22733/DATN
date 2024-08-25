package com.example.shoesmanagement.controller;

import com.example.shoesmanagement.model.*;
import com.example.shoesmanagement.repository.*;
import com.example.shoesmanagement.service.*;
import com.example.shoesmanagement.viewModel.GiayViewModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@RequestMapping("/manage/changehd/")
@Controller
public class SuaHoaDonOnline {

    private int tongSanPham = 0;

    private double giaBan = 0;

    private double tongTienSanPham = tongSanPham * giaBan;

    private double giaTienGiam = 0;

    private double tongTien = tongTienSanPham - giaTienGiam;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private GiayService giayService;

    @Autowired
    private SizeService sizeService;

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private MauSacService mauSacService;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private GiayRepository giayRepository;
    @Autowired
    private HinhAnhRepository hinhAnhRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private HangRepository hangRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private NhanVienService nhanVienService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;
    @Autowired
    private GiayViewModelService giayViewModelService;

    @Autowired
    private LSThanhToanService lsThanhToanService;
    @Autowired
    private GiayChiTietService giayChiTietService;

    @Autowired
    private HoaDonChiTIetService hoaDonChiTietService;

    @Autowired
    private ChucVuService chucVuService;
    @Autowired
    private HttpSession httpSession;


    private double dieuKienKhuyenMai = 0;
    @GetMapping("/online/{idHD}")
    private String manageBillOnline(@PathVariable UUID idHD, Model model, HttpSession session) {
        if (session.getAttribute("staffLogged") == null && session.getAttribute("managerLogged") == null) {
            return "redirect:/login";
        }
        // Đưa id của hóa đơn vào session
        session.setAttribute("idHoaDon", idHD);

        // Đưa id của hóa đơn vào model để sử dụng trong trang sua_hd_online
        model.addAttribute("idHD", idHD);

        // Lấy dữ liệu cần thiết và đưa vào model
        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        List<ChiTietGiay> chiTietGiayList = new ArrayList<>();
        List<Giay> giayList = giayService.getAllGiay();
        List<Size> sizeList = sizeService.getAllSize();
        List<MauSac> mauSacList = mauSacService.getALlMauSac();

        // Lấy hóa đơn cụ thể dựa vào idHD
        HoaDon hoaDon = hoaDonService.getOne(idHD);
        List<HoaDonChiTiet> hoaDonChiTiets = hoaDon.getHoaDonChiTiets();


        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("hoaDonChiTiets", hoaDonChiTiets);
        model.addAttribute("items", chiTietGiayList);
        model.addAttribute("giayList", giayList);
        model.addAttribute("sizeList", sizeList);
        model.addAttribute("mauSacList", mauSacList);
        model.addAttribute("listSanPham", listG);
        model.addAttribute("tongSP", hoaDon.getTongSP());
        model.addAttribute("tongTien", hoaDon.getTongTien());
        model.addAttribute("trangThai", hoaDon.getTrangThai());
        model.addAttribute("khachHang", hoaDon.getKhachHang());

        showData(model);
        showTab1(model);

        return "manage/sua_hd_online";
    }


    @PostMapping("/confirmCancelBill/{idHD}")
    public ResponseEntity<Map<String, String>> confirmCancelBill(@PathVariable UUID idHD, HttpSession session,Model model
    ) {
        Map<String, String> response = new HashMap<>();

        // Kiểm tra quyền đăng nhập
        if (session.getAttribute("managerLogged") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Lấy hóa đơn cụ thể dựa vào idHD
        HoaDon hoaDon = hoaDonService.getOne(idHD);
        model.addAttribute("hoaDon", hoaDon);

        // Cập nhật trạng thái hóa đơn nếu không có sản phẩm nào
        if (hoaDon.getHoaDonChiTiets().isEmpty()) {
            hoaDon.setTrangThai(5);
            hoaDonService.save(hoaDon); // Lưu lại thay đổi
            response.put("status", "updated");
        } else {
            response.put("status", "not_updated");
        }

        return ResponseEntity.ok(response);
    }


    private void showData(Model model) {

        List<HoaDon> listAllHoaDonOnline = hoaDonService.listHoaDonOnline();


        List<HoaDon> listAllHoaDonDangGiao = new ArrayList<>();
        List<HoaDon> listHoaDonOnlineBaking = new ArrayList<>();


        int soLuongHoaDonOnline = 0;
        int soLuongHoaDonHuy = 0;
        int soLuongHoaDonDaThanhToan = 0;
        int soLuongHoaDonChuaThanhToanNhanHang = 0;
        int soLuongHoaDonDangGiao = 0;
        int soLuongHoaDonBanking = 0;
        int soLuongHoaDonDaNhan = 0;
        int soLuongHoaDonChoXacNhan = 0;
        int soLuongHoaDonChoLayHang = 0;


        int soLuongHoaDonThanhToanKhiNhanHang = 0;

        double tongTienHoaDon = 0.0;
        if (listAllHoaDonOnline != null) {
            for (HoaDon x : listAllHoaDonOnline) {
                if (x.getTrangThai() == 6 || x.getTrangThai() == 7) {
//                    System.out.println("abc");
                } else {
                    tongTienHoaDon += x.getTongTien();
                }
            }
        }

        if (listAllHoaDonOnline != null) {
            for (HoaDon x : listAllHoaDonOnline) {
                if (x.getTrangThai() == 6 || x.getTrangThai() == 7) {
//                    System.out.println("abc");
                } else {
                    if (x.getHinhThucThanhToan() == 1) {
                        soLuongHoaDonBanking++;
                        listHoaDonOnlineBaking.add(x);
                    }
                    if (x.getHinhThucThanhToan() == 0) {
                        soLuongHoaDonThanhToanKhiNhanHang++;
                    }

                    if (x.getTrangThai() == 1 && x.getHinhThucThanhToan() == 1) {
                        soLuongHoaDonDaThanhToan++;
                    }
                    if (x.getTrangThai() == 4 && x.getHinhThucThanhToan() == 0) {
                        soLuongHoaDonDaThanhToan++;
                    }
                    if (x.getTrangThai() == 1) {
                        soLuongHoaDonChoXacNhan++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 2) {
                        soLuongHoaDonChoLayHang++;
                    }
                    if (x.getTrangThai() == 3) {
                        soLuongHoaDonDangGiao++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 3) {
                        soLuongHoaDonDangGiao++;
                        listAllHoaDonDangGiao.add(x);
                    }
                    if (x.getTrangThai() == 5) {
                        soLuongHoaDonHuy++;
                    }
                    if (x.getTrangThai() == 4) {
                        soLuongHoaDonDaNhan++;
                    }
                }
            }
        }

        int soLuongHoaDonChuaThanhToan = soLuongHoaDonChuaThanhToanNhanHang + soLuongHoaDonBanking;


        int soLuongHdHoanChoXacNhan = 0;
        int soLuongHdHoanKhachHuy = 0;


        model.addAttribute("sumBillOnline", soLuongHoaDonOnline);
        model.addAttribute("totalAmount", tongTienHoaDon);
        model.addAttribute("sumQuantityBaking", soLuongHoaDonBanking);
        model.addAttribute("sumQuantityDelivery", soLuongHoaDonThanhToanKhiNhanHang);
        model.addAttribute("hoaDonChuaThanhToan", soLuongHoaDonChuaThanhToan);
        model.addAttribute("hoaDonDaThanhToan", soLuongHoaDonDaThanhToan);
        model.addAttribute("hoaDonDangGiao", soLuongHoaDonDangGiao);
        model.addAttribute("hoaDonChoXacNhan", soLuongHoaDonChoXacNhan);
        model.addAttribute("hoaDonChoLayHang", soLuongHoaDonChoLayHang);
        model.addAttribute("hoaDonHuy", soLuongHoaDonHuy);
        model.addAttribute("hoaDonDaNhan", soLuongHoaDonDaNhan);

        model.addAttribute("soLuongHdHoanChoXacNhan", soLuongHdHoanChoXacNhan);
        model.addAttribute("soLuongHdHoanKhachHuy", soLuongHdHoanKhachHuy);

        model.addAttribute("listHoaDonOnline", listAllHoaDonOnline);
        model.addAttribute("listHoaDonOnlineGiaoHang", listAllHoaDonDangGiao);
    }
    private void showTab1(Model model) {
        model.addAttribute("activeAll", "nav-link active");
        model.addAttribute("xac_nhan_tt", "nav-link");
        model.addAttribute("van_chuyen", "nav-link");

        model.addAttribute("tabpane1", "tab-pane show active");
        model.addAttribute("tabpane2", "tab-pane");
        model.addAttribute("tabpane3", "tab-pane");
        model.addAttribute("tabpane4", "tab-pane");

    }

    @GetMapping("/chon-size2/{idGiay}/{mauSac}")
    public String chonSize(@PathVariable(value = "idGiay") UUID idGiay,
                           @PathVariable(value = "mauSac") String mauSac, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        UUID idHoaDon = (UUID) session.getAttribute("idHoaDon");

        // Kiểm tra nếu idHoaDon bị null và xử lý


        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);
        model.addAttribute("idHoaDon", idHoaDon);
        Giay giay = giayService.getByIdGiay(idGiay);
        List<ChiTietGiay> sizeList = sizeRepository.findByIdGiayAndMauSac2(idGiay, mauSac);

        model.addAttribute("gioHang", hoaDonChiTietService.findByIdHoaDon(idHoaDon));
        model.addAttribute("giay", giay);
        model.addAttribute("listChiTietGiay", sizeList);
        model.addAttribute("idHoaDon", idHoaDon);
        model.addAttribute("showModal1", true);
        model.addAttribute("tongTienSanPham", tongTienSanPham);
        model.addAttribute("tongTien", tongTien);

        return "/manage/sua_hd_online";
    }
    @GetMapping("/add-to-hd/{idHD}")
    public String addToCart(@PathVariable("idHD") UUID idHoaDon,
                            @RequestParam("idChiTietGiay") UUID idChiTietGiay,
                            @RequestParam("soLuong") int soLuong, Model model,
                            RedirectAttributes redirectAttributes, HttpSession session) {
        if (idHoaDon == null || idChiTietGiay == null) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Giá trị idHoaDon hoặc idChiTietGiay bị thiếu");
            return "redirect:/manage/changehd/online/";
        }

        System.out.println("idHoaDon: " + idHoaDon);
        System.out.println("idChiTietGiay: " + idChiTietGiay);
        System.out.println("soLuong: " + soLuong);

        List<GiayViewModel> listG = giayViewModelService.getAllVm();
        model.addAttribute("listSanPham", listG);

        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idChiTietGiay);
        if (soLuong > chiTietGiay.getSoLuong()) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Số lượng trong kho không đủ");
            return "redirect:/manage/changehd/online/" + idHoaDon;
        }

        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        model.addAttribute("idHoaDon", idHoaDon);

        List<HoaDonChiTiet> cart = (List<HoaDonChiTiet>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idChiTietGiay);
        if (hoaDonChiTiet != null) {
            // Giữ nguyên đơn giá, chỉ cập nhật số lượng
            hoaDonChiTiet.setSoLuong(hoaDonChiTiet.getSoLuong() + soLuong);
            hoaDonChiTiet.setTrangThai(1);
            hoaDonChiTietService.add(hoaDonChiTiet);
        } else {
            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct.setChiTietGiay(chiTietGiay);
            hdct.setHoaDon(hoaDon);
            hdct.setDonGia(chiTietGiay.getGiaBan());
            hdct.setSoLuong(soLuong);
            hdct.setTrangThai(1);
            hdct.setTgThem(new Date());
            tongSanPham++;
            session.setAttribute("tongSP", tongSanPham);
            hoaDonChiTietService.add(hdct);
            cart.add(hdct);
        }


        // Cập nhật số lượng tồn kho
        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() - soLuong);
        giayChiTietService.save(chiTietGiay);

        hoaDon.setTongTienSanPham(hoaDon.getTongTienSanPham() + chiTietGiay.getGiaBan() * soLuong);

        hoaDonService.save(hoaDon);
        hoaDonService.updateHoaDon(hoaDon);

        redirectAttributes.addFlashAttribute("messageSuccess", true);
        redirectAttributes.addFlashAttribute("tb", "Thêm vào giỏ hàng thành công");
        return "redirect:/manage/changehd/online/" + idHoaDon;
    }

    @GetMapping("/xoa-gio-hang2/{idChiTietGiay}")
    public String xoaSanPham2(@PathVariable("idChiTietGiay") UUID idChiTietGiay, RedirectAttributes redirectAttributes, Model model) {

        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idChiTietGiay);
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idChiTietGiay);

        // Lấy danh sách các sản phẩm trong hóa đơn
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietService.findByIdHoaDon(idHoaDon);

        // Nếu hóa đơn chỉ có 1 sản phẩm, hiển thị thông báo và không xóa
        if (listHDCT.size() <= 1) {
            redirectAttributes.addFlashAttribute("messageError", true);
            redirectAttributes.addFlashAttribute("tbaoError", "Hóa đơn phải có ít nhất 1 sản phẩm");
            return "redirect:/manage/changehd/online/" + idHoaDon;
        }

        // Cập nhật tổng tiền hóa đơn
        HoaDon hoaDon = hoaDonService.getOne(idHoaDon);
        hoaDon.setTongTien(hoaDon.getTongTien() - hoaDonChiTiet.getDonGia());
        hoaDon.setTongTienSanPham(hoaDon.getTongTienSanPham() - hoaDonChiTiet.getDonGia());
        hoaDonService.add(hoaDon);

        // Cập nhật số lượng sản phẩm và trạng thái của ChiTietGiay
        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() + hoaDonChiTiet.getSoLuong());
        chiTietGiay.setTrangThai(1);
        giayChiTietService.save(chiTietGiay);

        // Xóa chi tiết hóa đơn
        hoaDonChiTietService.deleteCTG(idChiTietGiay, idHoaDon);

        // Cập nhật số lượng sản phẩm trong giỏ hàng
        tongSanPham--;
        httpSession.setAttribute("tongSP", tongSanPham);
        hoaDonService.updateHoaDon(hoaDon);
        model.addAttribute("idCTG", idChiTietGiay);

        httpSession.removeAttribute("idChiTietGiay");
        redirectAttributes.addFlashAttribute("messageSuccess", true);
        redirectAttributes.addFlashAttribute("tb", "Xóa thành công");

        return "redirect:/manage/changehd/online/" + idHoaDon;
    }



    @PostMapping("/deleteChiTietGiay/{idCTG}/{idHD}")
    @ResponseBody
    public ResponseEntity<String> deleteChiTietGiay(@PathVariable UUID idCTG, @PathVariable UUID idHD, Model model) {
        try {
            System.out.println("Received idCTG: " + idCTG);
            System.out.println("Received idHD: " + idHD);

            HoaDon hoaDon = hoaDonService.getOne(idHD);
            ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idCTG);


            // Lấy thông tin chi tiết sản phẩm trong hóa đơn
            HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHD, idCTG);
            if (hoaDonChiTiet != null) {
                int soLuong = hoaDonChiTiet.getSoLuong();
                List<HoaDonChiTiet> listHDCT =  hoaDonChiTietService.findByIdHoaDon(idHD);
                if(listHDCT.size() <= 1){
                    return ResponseEntity.status(400).body("Không thể xóa sản phẩm. Hóa đơn phải có ít nhất một sản phẩm.");
                }else {

                    // Tăng số lượng sản phẩm trong kho
                    chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() + soLuong);
                    giayChiTietService.save(chiTietGiay);

                    // Xóa sản phẩm khỏi hóa đơn
                    hoaDonChiTietRepository.deleteHoaDonChiTietByChiTietGiay(chiTietGiay.getIdCTG(),hoaDon.getIdHD());

                    hoaDonService.updateHoaDon(hoaDon);
                    model.addAttribute("idCTG", idCTG);
                }
            } else {
                return ResponseEntity.status(404).body("Không tìm thấy chi tiết hóa đơn!");
            }

            return ResponseEntity.ok("Sản phẩm đã được xoá khỏi giỏ hàng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Không thể xóa sản phẩm. Hóa đơn phải có ít nhất một sản phẩm.");
        }
    }



    @PostMapping("/updateQuantity1")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateQuantity1(@RequestParam UUID idCTG, @RequestParam int quantity) {
        UUID idHoaDon = (UUID) httpSession.getAttribute("idHoaDon");
        ChiTietGiay chiTietGiay = giayChiTietService.getByIdChiTietGiay(idCTG);
        HoaDonChiTiet hoaDonChiTiet = hoaDonChiTietService.getOne(idHoaDon, idCTG);

        Map<String, Object> response = new HashMap<>();

        if (chiTietGiay == null || hoaDonChiTiet == null) {
            System.err.println("ChiTietGiay hoặc HoaDonChiTiet không tồn tại");
            response.put("error", "Sản phẩm không tồn tại");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        int previousQuantity = hoaDonChiTiet.getSoLuong();
        int quantityDifference = quantity - previousQuantity;

        // Kiểm tra xem số lượng mới có vượt quá số lượng tồn kho không
        if (chiTietGiay.getSoLuong() < quantityDifference) {
            response.put("error", "Số lượng trong kho không đủ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Nếu số lượng đủ, tiến hành cập nhật hóa đơn và tồn kho
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon).get();
        hoaDon.setKhuyenMai(null);
        hoaDonRepository.saveAndFlush(hoaDon);

        // Cập nhật số lượng trong hóa đơn
        hoaDonChiTiet.setSoLuong(quantity);
        double donGia = chiTietGiay.getGiaBan();
        hoaDonChiTiet.setDonGia(donGia);
        hoaDonChiTietService.add(hoaDonChiTiet);

        // Cập nhật số lượng trong kho
        chiTietGiay.setSoLuong(chiTietGiay.getSoLuong() - quantityDifference);
        giayChiTietService.update(chiTietGiay);

        // Tính toán lại tổng tiền của hóa đơn
        double totalProductAmount = hoaDon.getHoaDonChiTiets().stream()
                .mapToDouble(hdct -> hdct.getDonGia() * hdct.getSoLuong())
                .sum();
        double newTotalAmount = totalProductAmount + hoaDon.getTienShip();
        hoaDon.setTongTien(newTotalAmount);

        // Tính toán lại tổng số lượng sản phẩm trong hóa đơn
        int newTotalQuantity = hoaDon.getHoaDonChiTiets().stream().mapToInt(HoaDonChiTiet::getSoLuong).sum();
        hoaDon.setTongSP(newTotalQuantity);
        hoaDonService.add(hoaDon);
        hoaDonService.updateHoaDon(hoaDon);

        response.put("tongTienSanPham", newTotalAmount);
        response.put("tongSoLuongSanPham", newTotalQuantity);

        return ResponseEntity.ok(response);
    }






}
