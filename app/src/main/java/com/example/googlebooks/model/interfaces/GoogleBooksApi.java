package com.example.googlebooks.model.interfaces;

import com.example.googlebooks.model.ApiResponse;
import com.example.googlebooks.model.Book;
import com.example.googlebooks.model.VolumeListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GoogleBooksApi {

    //Obtener volúmenes
    @GET("volumes")
    Call<ApiResponse> searchBooks(
            @Query("q") String query,
            @Query("startIndex") int startIndex,
            @Query("maxResults") int maxResults);

    //Obtener detalles de un volúmen
    @GET("volumes/{volumeId}")
    Call<Book> getBookDetails(
            @Path("volumeId") String volumeId,
            @Query("key") String apiKey);


    //Obtener mi libreria
    @Headers("Accept: application/json")
    @GET("mylibrary/bookshelves")
    Call<BookshelvesResponse> getBookshelves(@Header("Authorization") String authorization);

    //Agregar a favoritos
    @POST("mylibrary/bookshelves/{shelfId}/addVolume")
    Call<Void> addToFavorites(@Path("shelfId") int shelfId,
                              @Query("volumeId") String volumeId,
                              @Header("Authorization") String authToken);

    //Obtener favoritos
    @GET("mylibrary/bookshelves/{shelfId}/volumes")
    Call<VolumeListResponse> getVolumesFromBookshelf(@Path("shelfId") String shelfId,
                                                     @Header("Authorization") String authToken);
}



