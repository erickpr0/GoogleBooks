package com.example.googlebooks.model;

import java.util.List;

public class VolumeInfo {
    private String title;
    private List<String> authors;
    private ImageLinks imageLinks;
    private String description;
    private int pageCount;
    private double averageRaiting;
    private String language;


    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public ImageLinks getImageLinks() {
        return imageLinks;
    }

    public String getDescription() {
        return description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public double getAverageRaiting() {
        return averageRaiting;
    }

    public String getLanguage() {
        return language;
    }
}
