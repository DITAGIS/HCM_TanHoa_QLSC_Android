package vn.ditagis.com.tanhoa.qlsc.entities;

public class HoSoThietBiSuCo {
    private String idSuCo;
    private double thoigianVanHanh;
    private String maThietBi;
    private String tenThietBi;

    public HoSoThietBiSuCo(String idSuCo, double soLuong, String maVatTu, String tenVatTu) {
        this.idSuCo = idSuCo;
        this.thoigianVanHanh = soLuong;
        this.maThietBi = maVatTu;
        this.tenThietBi = tenVatTu;
    }

    public String getIdSuCo() {
        return idSuCo;
    }

    public double getThoigianVanHanh() {
        return thoigianVanHanh;
    }

    public String getMaThietBi() {
        return maThietBi;
    }

    public String getTenThietBi() {
        return tenThietBi;
    }

}
