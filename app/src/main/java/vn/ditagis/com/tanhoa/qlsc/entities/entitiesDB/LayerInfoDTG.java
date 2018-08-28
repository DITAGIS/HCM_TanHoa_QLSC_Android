package vn.ditagis.com.tanhoa.qlsc.entities.entitiesDB;


public class LayerInfoDTG {


    private String id;
    private String titleLayer;
    private String url;
    private boolean isCreate;
    private boolean isDelete;
    private boolean isEdit;
    private boolean isView;
    private String definition;

    public LayerInfoDTG(String id, String titleLayer, String url, boolean isCreate, boolean isDelete, boolean isEdit, boolean isView, String definition) {
        this.id = id;
        this.titleLayer = titleLayer;
        this.url = url;
        this.isCreate = isCreate;
        this.isDelete = isDelete;
        this.isEdit = isEdit;
        this.isView = isView;
        this.definition = definition;
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
}
