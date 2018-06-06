package hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB;

import java.util.Date;

public class KhachHang {
    private String danhBa;
    private short hieuLuc;
    private String hopDong;
    private String maLoTrinh;
    private String dot;
    private String may;
    private String tenKH;
    private String duong;
    private String so;

    private String phuong;
    private String sdt;
    private String quan;
    private int gb;
    private int dm;
    private int sh;
    private int sx;
    private int dv;
    private int hc;
    private String hieu;
    private int co;
    private String soThan;
    private Date ngayGan;
    private String chiThan;
    private String chiCo;
    private String viTri;
    private boolean ganMoi;
    private int thongTinBaoThay;
    private String pin;

    public String getDanhBa() {
        return danhBa;
    }

    public void setDanhBa(String danhBa) {
        this.danhBa = danhBa;
    }

    public short getHieuLuc() {
        return hieuLuc;
    }

    public void setHieuLuc(short hieuLuc) {
        this.hieuLuc = hieuLuc;
    }

    public String getHopDong() {
        return hopDong;
    }

    public void setHopDong(String hopDong) {
        this.hopDong = hopDong;
    }

    public String getMaLoTrinh() {
        return maLoTrinh;
    }

    public void setMaLoTrinh(String maLoTrinh) {
        this.maLoTrinh = maLoTrinh;
    }

    public String getDot() {
        return dot;
    }

    public void setDot(String dot) {
        this.dot = dot;
    }

    public String getMay() {
        return may;
    }

    public void setMay(String may) {
        this.may = may;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSo() {
        return so;
    }

    public void setSo(String so) {
        this.so = so;
    }

    public String getDuong() {
        return duong;
    }

    public void setDuong(String duong) {
        this.duong = duong;
    }

    public String getPhuong() {
        return phuong;
    }

    public void setPhuong(String phuong) {
        this.phuong = phuong;
    }

    public String getQuan() {
        return quan;
    }

    public void setQuan(String quan) {
        this.quan = quan;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public int getGb() {
        return gb;
    }

    public void setGb(int gb) {
        this.gb = gb;
    }

    public int getDm() {
        return dm;
    }

    public void setDm(int dm) {
        this.dm = dm;
    }

    public int getSh() {
        return sh;
    }

    public void setSh(int sh) {
        this.sh = sh;
    }

    public int getSx() {
        return sx;
    }

    public void setSx(int sx) {
        this.sx = sx;
    }

    public int getDv() {
        return dv;
    }

    public void setDv(int dv) {
        this.dv = dv;
    }

    public int getHc() {
        return hc;
    }

    public void setHc(int hc) {
        this.hc = hc;
    }

    public String getHieu() {
        return hieu;
    }

    public void setHieu(String hieu) {
        this.hieu = hieu;
    }

    public int getCo() {
        return co;
    }

    public void setCo(int co) {
        this.co = co;
    }

    public String getSoThan() {
        return soThan;
    }

    public void setSoThan(String soThan) {
        this.soThan = soThan;
    }

    public Date getNgayGan() {
        return ngayGan;
    }

    public void setNgayGan(Date ngayGan) {
        this.ngayGan = ngayGan;
    }

    public String getChiThan() {
        return chiThan;
    }

    public void setChiThan(String chiThan) {
        this.chiThan = chiThan;
    }

    public String getChiCo() {
        return chiCo;
    }

    public void setChiCo(String chiCo) {
        this.chiCo = chiCo;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        this.viTri = viTri;
    }

    public boolean isGanMoi() {
        return ganMoi;
    }

    public void setGanMoi(boolean ganMoi) {
        this.ganMoi = ganMoi;
    }

    public int getThongTinBaoThay() {
        return thongTinBaoThay;
    }

    public void setThongTinBaoThay(int thongTinBaoThay) {
        this.thongTinBaoThay = thongTinBaoThay;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public KhachHang() {

    }

    public KhachHang(String danhBa, short hieuLuc, String hopDong, String maLoTrinh, String dot, String may, String tenKH, String so, String duong, String phuong, String quan, String sdt, int gb, int dm, int sh, int sx, int dv, int hc, String hieu, int co, String soThan, Date ngayGan, String chiThan, String chiCo, String viTri, boolean ganMoi, int thongTinBaoThay, String pin) {
        this.danhBa = danhBa;
        this.hieuLuc = hieuLuc;
        this.hopDong = hopDong;
        this.maLoTrinh = maLoTrinh;
        this.dot = dot;
        this.may = may;
        this.tenKH = tenKH;
        this.so = so;
        this.duong = duong;
        this.phuong = phuong;
        this.quan = quan;
        this.sdt = sdt;
        this.gb = gb;
        this.dm = dm;
        this.sh = sh;
        this.sx = sx;
        this.dv = dv;
        this.hc = hc;
        this.hieu = hieu;
        this.co = co;
        this.soThan = soThan;
        this.ngayGan = ngayGan;
        this.chiThan = chiThan;
        this.chiCo = chiCo;
        this.viTri = viTri;
        this.ganMoi = ganMoi;
        this.thongTinBaoThay = thongTinBaoThay;
        this.pin = pin;
    }
}
