package com.example.googlebooks.model.classes;

import com.google.gson.annotations.SerializedName;

public class Volume {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
