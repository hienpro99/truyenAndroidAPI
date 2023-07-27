package hienddph20890.fpoly.asg_hiendd.model;

public class Comment {
    private String userId;
    private String fullname;
    private String content;
    private String _id;
    private String createdAt;

    public Comment(String userId, String fullname, String content, String _id, String createdAt) {
        this.userId = userId;
        this.fullname = fullname;
        this.content = content;
        this._id = _id;
        this.createdAt = createdAt;
    }

    public Comment(String fullname, String content, String createdAt) {
        this.fullname = fullname;
        this.content = content;
        this.createdAt = createdAt;
    }
    // Các phương thức getter và setter cho các trường

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
