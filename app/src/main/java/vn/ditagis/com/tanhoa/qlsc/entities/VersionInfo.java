package vn.ditagis.com.tanhoa.qlsc.entities;

import java.util.Date;

public class VersionInfo {
    private String versionCode;
    private String type;
    private String link;
    private Date date;

    public VersionInfo(String versionCode, String type, String link, Date date) {
        this.versionCode = versionCode;
        this.type = type;
        this.link = link;
        this.date = date;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getType() {
        return type;
    }

    public String getLink() {
        return link;
    }

    public Date getDate() {
        return date;
    }
}
