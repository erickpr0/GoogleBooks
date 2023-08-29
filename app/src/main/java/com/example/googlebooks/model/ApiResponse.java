package com.example.googlebooks.model;

import java.util.List;

public class ApiResponse {
    private List<Book> items;
    private BookDetails bookDetails;

    public List<Book> getItems() {
        return items;
    }

    public BookDetails getVolume() {
        return bookDetails;
    }
}


