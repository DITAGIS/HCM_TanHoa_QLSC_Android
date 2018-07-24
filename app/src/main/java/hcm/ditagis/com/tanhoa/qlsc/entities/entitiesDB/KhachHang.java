package hcm.ditagis.com.tanhoa.qlsc.entities.entitiesDB;

public class KhachHang {
    private String userName;
    private String passWord;
    private String displayName;
    private String groupRole;
    private String role;

    public KhachHang() {

    }

    public KhachHang(String userName, String passWord, String nameDisplay) {
        this.userName = userName;
        this.passWord = passWord;
        this.displayName = nameDisplay;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getRole() {
        return role;
    }

    public String getGroupRole() {
        return groupRole;
    }

    public void setGroupRole(String groupRole) {
        this.groupRole = groupRole;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
