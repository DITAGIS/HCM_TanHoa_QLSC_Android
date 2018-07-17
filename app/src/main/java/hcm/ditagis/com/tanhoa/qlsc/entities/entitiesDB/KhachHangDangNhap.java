package hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB;

public class KhachHangDangNhap {
    private KhachHang khachHang;

    private KhachHangDangNhap() {

    }

    private static KhachHangDangNhap instance = null;

    public static KhachHangDangNhap getInstance() {
        if (instance == null) {
            instance = new KhachHangDangNhap();
        }
        return instance;
    }

    public void setKhachHang(KhachHang kh) {
        khachHang = kh;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }
}
