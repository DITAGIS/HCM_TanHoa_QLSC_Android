package vn.ditagis.com.tanhoa.qlsc.entities;


import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;

/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

public class DFeatureLayer {


    private FeatureLayer layer;
    private ServiceFeatureTable serviceFeatureTableSuCoThonTin;
    private DLayerInfo layerInfoDTG;
    private ServiceFeatureTable serviceFeatureTableHoSoVatTuSuCo;

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

    public void setServiceFeatureTableSuCoThonTin(ServiceFeatureTable serviceFeatureTableSuCoThonTin) {

        this.serviceFeatureTableSuCoThonTin = serviceFeatureTableSuCoThonTin;
    }

    public ServiceFeatureTable getServiceFeatureTableSuCoThonTin() {
        return serviceFeatureTableSuCoThonTin;
    }


    public DLayerInfo getLayerInfoDTG() {
        return layerInfoDTG;
    }

}
