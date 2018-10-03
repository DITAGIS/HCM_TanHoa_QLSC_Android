package vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB;

import java.util.List;

import vn.ditagis.com.tanhoa.qlsc.entities.DLayerInfo;
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoThietBiSuCo;
import vn.ditagis.com.tanhoa.qlsc.entities.HoSoVatTuSuCo;
import vn.ditagis.com.tanhoa.qlsc.entities.ThietBi;
import vn.ditagis.com.tanhoa.qlsc.entities.VatTu;


public class ListObjectDB {

    private static ListObjectDB instance = null;
    private List<VatTu> vatTuOngNganhs;
    private List<VatTu> vatTus;
    private List<ThietBi> thietBis;
    private List<String> dmas;
    private List<DLayerInfo> lstFeatureLayerDTG;
    private List<HoSoVatTuSuCo> lstHoSoVatTuSuCoInsert;
    private List<HoSoVatTuSuCo> hoSoVatTuSuCos;
    private List<HoSoThietBiSuCo> lstHoSoThietBiSuCoInsert;
    private List<HoSoThietBiSuCo> hoSoThietBiSuCos;

    public List<HoSoThietBiSuCo> getLstHoSoThietBiSuCoInsert() {
        return lstHoSoThietBiSuCoInsert;
    }

    public void setLstHoSoThietBiSuCoInsert(List<HoSoThietBiSuCo> lstHoSoThietBiSuCoInsert) {
        this.lstHoSoThietBiSuCoInsert = lstHoSoThietBiSuCoInsert;
    }

    public List<HoSoThietBiSuCo> getHoSoThietBiSuCos() {
        return hoSoThietBiSuCos;
    }

    public void setHoSoThietBiSuCos(List<HoSoThietBiSuCo> hoSoThietBiSuCos) {
        this.hoSoThietBiSuCos = hoSoThietBiSuCos;
    }

    private ListObjectDB() {
    }

    public static ListObjectDB getInstance() {
        if (instance == null)
            instance = new ListObjectDB();
        return instance;
    }

    public List<ThietBi> getThietBis() {
        return thietBis;
    }

    public void setThietBis(List<ThietBi> thietBis) {
        this.thietBis = thietBis;
    }

    public void clearListHoSoVatTuSuCoChange() {
        lstHoSoVatTuSuCoInsert.clear();
    }
    public void clearListHoSoThietBiSuCoChange() {
        lstHoSoThietBiSuCoInsert.clear();
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
        if (hoSoVatTuSuCos != null)
            hoSoVatTuSuCos.clear();
    }
    public void clearHoSoThietBiSuCos() {
        if (hoSoThietBiSuCos != null)
            hoSoThietBiSuCos.clear();
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
