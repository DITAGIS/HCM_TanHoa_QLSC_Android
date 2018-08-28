package vn.ditagis.com.tanhoa.qlsc.entities;

import com.esri.arcgisruntime.geometry.Point;

import java.util.Date;

public class DiemSuCo {
    public String idSuCo;
    public short trangThai;
    public String ghiChu;
    public String nguoiCapNhat;
    public Date ngayCapNhat;
    public Date ngayThongBao;
    public String vitri;
    public String sdt;
    public String nguyenNhan;
    public Point point;
    public byte[] image;

    public DiemSuCo() {
    }

    public Point getPoint() {
        return point;
    }

    public byte[] getImage() {
        return image;
    }

    public void setIdSuCo(String idSuCo) {
        this.idSuCo = idSuCo;
    }

    public void setTrangThai(short trangThai) {
        this.trangThai = trangThai;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public void setNguoiCapNhat(String nguoiCapNhat) {
        this.nguoiCapNhat = nguoiCapNhat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public void setNgayThongBao(Date ngayThongBao) {
        this.ngayThongBao = ngayThongBao;
    }

    public void setVitri(String vitri) {
        this.vitri = vitri;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public void setNguyenNhan(String nguyenNhan) {
        this.nguyenNhan = nguyenNhan;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getIdSuCo() {
        return idSuCo;
    }

    public short getTrangThai() {
        return trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public String getNguoiCapNhat() {
        return nguoiCapNhat;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public Date getNgayThongBao() {
        return ngayThongBao;
    }

    public String getVitri() {
        return vitri;
    }

    public String getSdt() {
        return sdt;
    }

    public String getNguyenNhan() {
        return nguyenNhan;
    }
}
