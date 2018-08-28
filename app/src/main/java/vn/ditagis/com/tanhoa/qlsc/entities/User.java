package vn.ditagis.com.tanhoa.qlsc.entities;

public class User {
    private String userName;
    private String passWord;
    private String displayName;

    private boolean isCreate;
    private boolean isValid;
    private String token;
    private String role;


    public User() {
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCreate() {
        return isCreate;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }
}