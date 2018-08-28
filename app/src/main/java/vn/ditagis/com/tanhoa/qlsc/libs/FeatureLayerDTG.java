package vn.ditagis.com.tanhoa.qlsc.libs;


import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.MapView;

import vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB.LayerInfoDTG;

/**
 * Created by NGUYEN HONG on 3/14/2018.
 */
public class FeatureLayerDTG {


    private FeatureLayer layer;

    private LayerInfoDTG layerInfoDTG;

    public FeatureLayerDTG(FeatureLayer layer, LayerInfoDTG layerInfoDTG) {
        this.layer = layer;
        this.layerInfoDTG = layerInfoDTG;
    }

    public FeatureLayer getLayer() {
        return layer;
    }

    public void setLayer(FeatureLayer layer) {
        this.layer = layer;
    }

    public LayerInfoDTG getLayerInfoDTG() {
        return layerInfoDTG;
    }

    public void setLayerInfoDTG(LayerInfoDTG layerInfoDTG) {
        this.layerInfoDTG = layerInfoDTG;
    }
}
