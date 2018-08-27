package vn.com.capnuoctanhoa.qlsc.libs;


import com.esri.arcgisruntime.layers.FeatureLayer;

/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

public class FeatureLayerDTG {
    private FeatureLayer featureLayer;
    private String[] addFields;
    private String[] updateFields;
    private String[] queryFields;
    private String[] outFields;
    private String titleLayer;
    private boolean isShowOnMap;

    public String[] getUpdateFields() {
        return updateFields;
    }

    public void setUpdateFields(String[] updateFields) {
        this.updateFields = updateFields;
    }

    public FeatureLayerDTG(FeatureLayer featureLayer, String titleLayer, boolean isShowOnMap) {
        this.featureLayer = featureLayer;
        this.titleLayer = titleLayer;
        this.isShowOnMap = isShowOnMap;
    }

    public boolean isShowOnMap() {
        return isShowOnMap;
    }

    public void setShowOnMap(boolean showOnMap) {
        isShowOnMap = showOnMap;
    }

    public FeatureLayer getFeatureLayer() {
        return featureLayer;
    }

    public String[] getAddFields() {
        return addFields;
    }

    public void setAddFields(String[] addFields) {
        this.addFields = addFields;
    }

    public String[] getOutFields() {
        return outFields;
    }

    public void setOutFields(String[] outFields) {
        this.outFields = outFields;
    }

    public String[] getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(String[] queryFields) {
        this.queryFields = queryFields;
    }

    public String getTitleLayer() {
        return titleLayer;
    }

    public void setTitleLayer(String titleLayer) {
        this.titleLayer = titleLayer;
    }
}
