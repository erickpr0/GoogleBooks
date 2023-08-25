package com.example.googlebooks.model;

import java.util.List;

public class VolumeInfo {
    private String title;
    private List<String> authors;
    private ImageLinks imageLinks;

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public ImageLinks getImageLinks() {
        return imageLinks;
    }
}
