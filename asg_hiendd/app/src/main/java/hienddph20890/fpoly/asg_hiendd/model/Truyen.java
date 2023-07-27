package hienddph20890.fpoly.asg_hiendd.model;

import java.io.Serializable;
import java.util.List;

public class Truyen implements Serializable {
    private String id;
    private String name;
    private String description;
    private String author;
    private int yearPublished; // Trường yearPublished được thêm vào
    private String coverImage;
    private List<String> images;
    private List<Comment> comments;

    public Truyen(String id, String name, String description, String author, int yearPublished, String coverImage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.author = author;
        this.yearPublished = yearPublished;
        this.coverImage = coverImage;
    }

    public Truyen(String name, String description, String author, int yearPublished, String coverImage) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.yearPublished = yearPublished;
        this.coverImage = coverImage;
    }

    public Truyen(String name, String description, String author) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(int yearPublished) {
        this.yearPublished = yearPublished;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
