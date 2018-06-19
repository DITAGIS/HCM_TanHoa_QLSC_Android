package hcm.ditagis.com.tanhoa.qlsc.entities;

public class HoSoVatTuSuCo {
    private String idSuCo;
    private int soLuong;
    private String maVatTu;


    public HoSoVatTuSuCo(String idSuCo, int soLuong, String maVatTu) {
        this.idSuCo = idSuCo;
        this.soLuong = soLuong;
        this.maVatTu = maVatTu;
    }

    public String getIdSuCo() {
        return idSuCo;
    }

    public void setIdSuCo(String idSuCo) {
        this.idSuCo = idSuCo;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getMaVatTu() {
        return maVatTu;
    }

    public void setMaVatTu(String maVatTu) {
        this.maVatTu = maVatTu;
    }
}
