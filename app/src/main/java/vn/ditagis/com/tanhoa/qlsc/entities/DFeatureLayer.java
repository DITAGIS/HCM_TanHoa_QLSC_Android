package vn.ditagis.com.tanhoa.qlsc.entities;


import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;

/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

public class DFeatureLayer {


    private FeatureLayer layer;
    private DLayerInfo layerInfoDTG;
    private ServiceFeatureTable serviceFeatureTableSuCoThongTin;
    private ServiceFeatureTable serviceFeatureTableHoSoVatTuSuCo;
    private ServiceFeatureTable serviceFeatureTableSuCoThietBi;
    private ServiceFeatureTable serviceFeatureTableHoSoThietBiSuCo;

    public ServiceFeatureTable getServiceFeatureTableSuCoThietBi() {
        return serviceFeatureTableSuCoThietBi;
    }

    public void setServiceFeatureTableSuCoThietBi(ServiceFeatureTable serviceFeatureTableSuCoThietBi) {
        this.serviceFeatureTableSuCoThietBi = serviceFeatureTableSuCoThietBi;
    }

    public ServiceFeatureTable getServiceFeatureTableHoSoThietBiSuCo() {
        return serviceFeatureTableHoSoThietBiSuCo;
    }

    public void setServiceFeatureTableHoSoThietBiSuCo(ServiceFeatureTable serviceFeatureTableHoSoThietBiSuCo) {
        this.serviceFeatureTableHoSoThietBiSuCo = serviceFeatureTableHoSoThietBiSuCo;
    }

    public DFeatureLayer() {

    }

    public ServiceFeatureTable getServiceFeatureTableHoSoVatTuSuCo() {
        return serviceFeatureTableHoSoVatTuSuCo;
    }

    public void setServiceFeatureTableHoSoVatTuSuCo(ServiceFeatureTable serviceFeatureTableHoSoVatTuSuCo) {
        this.serviceFeatureTableHoSoVatTuSuCo = serviceFeatureTableHoSoVatTuSuCo;
    }

    public void setLayer(FeatureLayer layer) {
        this.layer = layer;
    }

    public void setLayerInfoDTG(DLayerInfo layerInfoDTG) {
        this.layerInfoDTG = layerInfoDTG;
    }

    public FeatureLayer getLayer() {
        return layer;
    }

    public void setServiceFeatureTableSuCoThongTin(ServiceFeatureTable serviceFeatureTableSuCoThongTin) {

        this.serviceFeatureTableSuCoThongTin = serviceFeatureTableSuCoThongTin;
    }

    public ServiceFeatureTable getServiceFeatureTableSuCoThongTin() {
        return serviceFeatureTableSuCoThongTin;
    }


    public DLayerInfo getLayerInfoDTG() {
        return layerInfoDTG;
    }

}
