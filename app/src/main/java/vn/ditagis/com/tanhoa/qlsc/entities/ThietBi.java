package vn.ditagis.com.tanhoa.qlsc.entities;

public class ThietBi {
    private String maThietBi;
    private String tenThietBi;

    public ThietBi(String maVatTu, String tenVatTu) {
        this.maThietBi = maVatTu;
        this.tenThietBi = tenVatTu;
    }

    public String getMaThietBi() {
        return maThietBi;
    }

    public void setMaThietBi(String maThietBi) {
        this.maThietBi = maThietBi;
    }

    public String getTenThietBi() {
        return tenThietBi;
    }

    public void setTenThietBi(String tenThietBi) {
        this.tenThietBi = tenThietBi;
    }

}