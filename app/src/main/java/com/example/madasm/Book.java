package com.example.madasm;

import java.io.Serializable;

public class Book implements Serializable {

    private String title;
    private String author;
    private String description;
    private int coverImage;

    public Book(String title, String author, String description, int coverImage) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.coverImage = coverImage;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public int getCoverImage() {
        return coverImage;
    }
}

