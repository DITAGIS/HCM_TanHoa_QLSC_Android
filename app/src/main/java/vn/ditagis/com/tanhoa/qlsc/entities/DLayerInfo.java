package vn.ditagis.com.tanhoa.qlsc.entities;


/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

public class DLayerInfo {


    private String id;
    private String titleLayer;
    private String url;
    private boolean isCreate;
    private boolean isDelete;
    private boolean isEdit;
    private boolean isView;
    private String definition;
    private String outFields;
    private String addFields;
    private String updateFields;

    public DLayerInfo(String id, String titleLayer, String url, boolean isCreate, boolean isDelete, boolean isEdit, boolean isView, String definition, String outFields, String addFields, String updateFields) {
        this.id = id;
        this.titleLayer = titleLayer;
        this.url = url;
        this.isCreate = isCreate;
        this.isDelete = isDelete;
        this.isEdit = isEdit;
        this.isView = isView;
        this.definition = definition;
        this.outFields = outFields;
        this.addFields = addFields;
        this.updateFields = updateFields;
    }

    public String getId() {
        return id;
    }

    public String getTitleLayer() {
        return titleLayer;
    }

    public String getUrl() {
        return url;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public boolean isView() {
        return isView;
    }

    public String getDefinition() {
        return definition;
    }

    public String getOutFields() {
        return outFields;
    }

    public String getAddFields() {
        return addFields;
    }

    public String getUpdateFields() {
        return updateFields;
    }
}
