package com.example.googlebooks.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.googlebooks.R;
import com.example.googlebooks.model.Book;
import com.example.googlebooks.model.interfaces.GoogleBooksApi;
import com.example.googlebooks.network.ApiClient;
import com.example.googlebooks.ui.fragments.BookListFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BookListFragment bookListFragment = new BookListFragment();
        if (transaction.isEmpty()) {
            transaction.add(R.id.container, bookListFragment).commit();
        } else {
            transaction = null;
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, bookListFragment).commit();
        }

        Call<Book> call = ApiClient.getClient()
                .create(GoogleBooksApi.class)
                .searchBooks("Harry Potter");

        call.enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.isSuccessful()) {
                    Book apiResponse = response.body();
                    Log.d("book", "onResponse: " + response);
                    // Procesar la lista de libros aquí
                } else {
                    // Manejar errores aquí
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {

            }

        });
    }
}