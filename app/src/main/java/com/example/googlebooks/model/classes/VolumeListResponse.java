package com.example.googlebooks.model.classes;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VolumeListResponse {
    @SerializedName("items")
    private List<Book> items;

    public List<Book> getItems() {
        return items;
    }
}
