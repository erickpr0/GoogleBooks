package com.example.googlebooks.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlebooks.R;
import com.example.googlebooks.model.ApiResponse;
import com.example.googlebooks.model.Book;
import com.example.googlebooks.model.interfaces.GoogleBooksApi;
import com.example.googlebooks.network.ApiClient;
import com.example.googlebooks.ui.adapters.BookAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookListFragment extends Fragment {
    private BookAdapter bookAdapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String searchTxt = requireArguments().getString("searchTxt");
        recyclerView = view.findViewById(R.id.bookList);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        Call<ApiResponse> call = ApiClient.getClient()
                .create(GoogleBooksApi.class)
                .searchBooks(searchTxt);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    List<Book> books = apiResponse.getItems();

                    bookAdapter = new BookAdapter(books, requireContext());
                    recyclerView.setAdapter(bookAdapter);

                    // Procesar la lista de libros aquí
                    for (Book book : books) {
                        String title = book.getVolumeInfo().getTitle();
                        List<String> authors = book.getVolumeInfo().getAuthors();
                        Log.d("book", "onResponse: " + title);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
