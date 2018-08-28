package vn.ditagis.com.tanhoa.qlsc.entities;


import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;

/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

public class DFeatureLayer {


    private FeatureLayer layer;
    private ServiceFeatureTable serviceFeatureTable;
    private DLayerInfo layerInfoDTG;

    public DFeatureLayer(){

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

    public void setServiceFeatureTable(ServiceFeatureTable serviceFeatureTable) {

        this.serviceFeatureTable = serviceFeatureTable;
    }

    public ServiceFeatureTable getServiceFeatureTable() {
        return serviceFeatureTable;
    }


    public DLayerInfo getLayerInfoDTG() {
        return layerInfoDTG;
    }

}
