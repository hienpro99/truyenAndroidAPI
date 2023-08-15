package hienddph20890.fpoly.asg_hiendd.model;

public class User {
    private String userId;
    private String username;
    private String fullname;
    private String email;
    private Boolean Admin;

    public User(String userId, String username, String fullname,String email, Boolean Admin) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullname = fullname;
        this.Admin = Admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return Admin;
    }

    public void setAdmin(Boolean admin) {
        Admin = admin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
