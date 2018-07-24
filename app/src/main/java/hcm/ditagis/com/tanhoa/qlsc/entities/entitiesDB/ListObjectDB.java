package hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB;

import java.util.List;

import hcm.ditagis.com.tanhoa.qlsc.entities.VatTu;


public class ListObjectDB {

    private static ListObjectDB instance = null;
    private List<VatTu> vatTuOngNganhs;
    private List<VatTu> vatTuOngChinhs;
    private List<String> dmas;
    private List<LayerInfoDTG> lstFeatureLayerDTG;

    private ListObjectDB() {
    }

    public static ListObjectDB getInstance() {
        if (instance == null)
            instance = new ListObjectDB();
        return instance;
    }

    public List<VatTu> getVatTuOngNganhs() {
        return vatTuOngNganhs;
    }

    public void setVatTuOngNganhs(List<VatTu> vatTuOngNganhs) {
        this.vatTuOngNganhs = vatTuOngNganhs;
    }

    public List<VatTu> getVatTuOngChinhs() {
        return vatTuOngChinhs;
    }

    public void setVatTuOngChinhs(List<VatTu> vatTuOngChinhs) {
        this.vatTuOngChinhs = vatTuOngChinhs;
    }

    public List<String> getDmas() {
        return dmas;
    }

    public void setDmas(List<String> dmas) {
        this.dmas = dmas;
    }

    public List<LayerInfoDTG> getListLayerInfoDTG() {
        return lstFeatureLayerDTG;
    }

    public void setLstFeatureLayerDTG(List<LayerInfoDTG> lstFeatureLayerDTG) {
        this.lstFeatureLayerDTG = lstFeatureLayerDTG;
    }
}
