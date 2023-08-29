package com.example.googlebooks.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.googlebooks.R;
import com.example.googlebooks.ui.fragments.BookListFragment;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = findViewById(R.id.progress);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                querySearch(savedInstanceState, query);
                progress.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void querySearch(Bundle savedInstanceState, String query) {
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString("searchTxt", query);


            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.container, BookListFragment.class, bundle)
                    .addToBackStack(null)
                    .commit();
        }
    }
}