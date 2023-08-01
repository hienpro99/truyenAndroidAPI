package hienddph20890.fpoly.asg_hiendd.model;

public class User {
    private String userId;
    private String username;
    private String fullname;

    public User(String userId, String username, String fullname) {
        this.userId = userId;
        this.username = username;
        this.fullname = fullname;
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
