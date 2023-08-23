package com.example.googlebooks.model.interfaces;

import com.example.googlebooks.model.Book;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleBooksApi {
    @GET("volumes")
    Call<Book> searchBooks(@Query("q") String query);
}

