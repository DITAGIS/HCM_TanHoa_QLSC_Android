package vn.com.capnuoctanhoa.qlsc.utities;

/**
 * Created by NGUYEN HONG on 3/20/2018.
 */

public class Config {
    private String url;
    private String alias;
    private String id;
    private String[] addFields;
    private String[] updateFields;
    private String[] queryFields;
    private String[] outFields;
    private boolean isShowOnMap = false;
    private static Config instance = null;

    public static Config getInstance() {
        if (instance == null)
            instance = new Config();
        return instance;
    }

    private Config() {
    }

    public String[] getUpdateFields() {
        return updateFields;
    }

    public void setUpdateFields(String[] updateFields) {
        this.updateFields = updateFields;
    }

    public String[] getAddFields() {
        return addFields;
    }

    public void setAddFields(String[] addFields) {
        this.addFields = addFields;
    }

    public boolean isShowOnMap() {
        return isShowOnMap;
    }

    public void setShowOnMap(boolean showOnMap) {
        isShowOnMap = showOnMap;
    }

    public Config(String url, String alias, String id,String[] addField, String[] updateField, String[] queryField, String[] outField, boolean isShowOnMap) {
        this.url = url;
        this.alias = alias;
        this.id = id;
        this.addFields = addField;
        this.updateFields = updateField;
        this.queryFields = queryField;
        this.outFields = outField;
        this.isShowOnMap = isShowOnMap;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(String[] queryFields) {
        this.queryFields = queryFields;
    }

    public String[] getOutFields() {
        return outFields;
    }

    public void setOutFields(String[] outFields) {
        this.outFields = outFields;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}