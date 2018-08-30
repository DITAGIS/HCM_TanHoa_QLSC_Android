package vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB;

import java.util.List;

import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo;
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo;
import vn.ditagis.com.tanhoa.qlsc.entities.VatTu;


public class ListObjectDB {

    private static ListObjectDB instance = null;
    private List<VatTu> vatTuOngNganhs;
    private List<VatTu> vatTus;
    private List<String> dmas;
    private List<DLayerInfo> lstFeatureLayerDTG;
    private List<HoSoVatTuSuCo> lstHoSoVatTuSuCoInsert;
    private List<HoSoVatTuSuCo> hoSoVatTuSuCos;

    private ListObjectDB() {
    }

    public static ListObjectDB getInstance() {
        if (instance == null)
            instance = new ListObjectDB();
        return instance;
    }

    public void clearListHoSoVatTuSuCoChange() {
        lstHoSoVatTuSuCoInsert.clear();
    }

    public List<HoSoVatTuSuCo> getLstHoSoVatTuSuCoInsert() {
        return lstHoSoVatTuSuCoInsert;
    }

    public void setLstHoSoVatTuSuCoInsert(List<HoSoVatTuSuCo> lstHoSoVatTuSuCoInsert) {
        this.lstHoSoVatTuSuCoInsert = lstHoSoVatTuSuCoInsert;
    }

    public List<HoSoVatTuSuCo> getHoSoVatTuSuCos() {
        return hoSoVatTuSuCos;
    }

    public void clearHoSoVatTuSuCos() {
        if(hoSoVatTuSuCos!= null)
            hoSoVatTuSuCos.clear();
    }

    public void setHoSoVatTuSuCos(List<HoSoVatTuSuCo> hoSoVatTuSuCos) {
        this.hoSoVatTuSuCos = hoSoVatTuSuCos;
    }

    public List<VatTu> getVatTuOngNganhs() {
        return vatTuOngNganhs;
    }

    public void setVatTuOngNganhs(List<VatTu> vatTuOngNganhs) {
        this.vatTuOngNganhs = vatTuOngNganhs;
    }

    public List<VatTu> getVatTus() {
        return vatTus;
    }

    public void setVatTus(List<VatTu> vatTus) {
        this.vatTus = vatTus;
    }

    public List<String> getDmas() {
        return dmas;
    }

    public void setDmas(List<String> dmas) {
        this.dmas = dmas;
    }

    public List<DLayerInfo> getLstFeatureLayerDTG() {
        return lstFeatureLayerDTG;
    }

    public void setLstFeatureLayerDTG(List<DLayerInfo> lstFeatureLayerDTG) {
        this.lstFeatureLayerDTG = lstFeatureLayerDTG;
    }
}
