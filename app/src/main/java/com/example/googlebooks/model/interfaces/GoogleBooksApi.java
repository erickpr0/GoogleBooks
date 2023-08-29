package com.example.googlebooks.model.interfaces;

import com.example.googlebooks.model.ApiResponse;
import com.example.googlebooks.model.Book;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GoogleBooksApi {
    @GET("volumes")
    Call<ApiResponse> searchBooks(
            @Query("q") String query,
            @Query("startIndex") int startIndex,
            @Query("maxResults") int maxResults);

    @GET("volumes/{volumeId}")
    Call<Book> getBookDetails(
            @Path("volumeId") String volumeId,
            @Query("key") String apiKey);
}

