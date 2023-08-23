package com.example.googlebooks.model;

import java.util.List;

public class VolumeInfo {
    private String title;
    private List<String> authors;
    private String description;

    public String getTitle() {
        return title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getDescription() {
        return description;
    }
}
