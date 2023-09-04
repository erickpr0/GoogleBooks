package com.example.googlebooks.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlebooks.R;
import com.example.googlebooks.model.classes.ApiResponse;
import com.example.googlebooks.model.classes.Book;
import com.example.googlebooks.model.classes.VolumeListResponse;
import com.example.googlebooks.model.interfaces.GoogleBooksApi;
import com.example.googlebooks.network.ApiClient;
import com.example.googlebooks.ui.activities.MainActivity;
import com.example.googlebooks.ui.adapters.BookAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookListFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    private static BookAdapter bookAdapter = new BookAdapter();
    private final String API_KEY = "AIzaSyCDSQUakLIyVzQzgu3tQpDenDl9QCCSdHU";
    private static RecyclerView recyclerView;
    private int currentPage = 0;
    boolean isLoading = false;
    private String searchTxt;
    private static List<Book> books;
    private static List<Book> previous;
    private static String accessToken;
    private static boolean favoriesActive = false;
    private boolean favorites;
    //private static ArrayList<Bookshelf> bookshelves;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchTxt = requireArguments().getString("searchTxt");
        accessToken = requireArguments().getString("token");
        //bookshelves = requireArguments().getParcelableArrayList("shelves");

        recyclerView = view.findViewById(R.id.bookList);
        ImageView filter = view.findViewById(R.id.filter);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        Call<ApiResponse> call = ApiClient.getClient().create(GoogleBooksApi.class).searchBooks(searchTxt, 0, 10);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("ibm", "onResponse: averrr" + response.body());
                    MainActivity.progress.setVisibility(View.GONE);
                    ApiResponse apiResponse = response.body();
                    assert apiResponse != null;
                    books = apiResponse.getItems();

                    bookAdapter = new BookAdapter(books, requireContext());
                    recyclerView.setAdapter(bookAdapter);

                    bookAdapter.setOnClickListener(position -> {
                        Book book = books.get(position);

                        Call<Book> callDetails = ApiClient.getClient().create(GoogleBooksApi.class).getBookDetails(book.getId(), API_KEY);

                        callDetails.enqueue(new Callback<Book>() {
                            @Override
                            public void onResponse(@NonNull Call<Book> call1, @NonNull Response<Book> response1) {
                                if (response1.isSuccessful()) {
                                    Book b = response1.body();
                                    assert response1.body() != null;
                                    assert b != null;
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("book", b);
                                    bundle.putString("token", accessToken);
                                    requireActivity().getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.container, DetailsFragment.class, bundle).addToBackStack(null).commit();
                                } else {
                                    Log.d("ibm", "onResponse: " + response1.body());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Book> call1, @NonNull Throwable t) {
                                Log.e("ibm", "onFailure: " + t);
                            }
                        });
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }

        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadNextPage();
                }
            }
        });

        favorites = false;
        filter.setOnClickListener(v -> {
            if (favorites) {
                Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_sub_buttons);
                filter.setAnimation(animation);
                filter.setImageResource(R.drawable.heart_selector);
                clearFavorites();
                favorites = false;
            } else {
                filter.setImageResource(R.drawable.ic_heart_red);
                Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_sub_buttons);
                filter.setAnimation(animation);
                showFavorites(accessToken, requireContext());
                favorites = true;
            }
        });
    }

    private void loadNextPage() {
        if (!favoriesActive) {
            isLoading = true;
            int itemsPerPage = 10;
            int startIndex = currentPage * itemsPerPage;

            Call<ApiResponse> call = ApiClient.getClient().create(GoogleBooksApi.class).searchBooks(searchTxt, startIndex, 10);
            call.enqueue(new Callback<ApiResponse>() {

                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    isLoading = false;
                    if (response.isSuccessful()) {
                        ApiResponse apiResponse = response.body();
                        assert apiResponse != null;
                        List<Book> newBooks = apiResponse.getItems();
                        bookAdapter.addBooks(newBooks);
                        currentPage++;
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    isLoading = false;
                }
            });
        }
    }

    public void showFavorites(String accessToken, Context context) {
        Call<VolumeListResponse> call = ApiClient.getClient().create(GoogleBooksApi.class).getVolumesFromBookshelf("0", "Bearer " + accessToken); //favoritos

        call.enqueue(new Callback<VolumeListResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<VolumeListResponse> call, @NonNull Response<VolumeListResponse> response) {
                if (response.isSuccessful()) {
                    VolumeListResponse volumeListResponse = response.body();
                    assert volumeListResponse != null;
                    List<Book> volumes = volumeListResponse.getItems();

                    previous = new ArrayList<>();
                    previous.addAll(books);
                    books.clear();
                    books.addAll(volumes);

                    bookAdapter.notifyDataSetChanged();
                    favoriesActive = true;

                    // Handle the retrieved list of volumes here
                } else {
                    Log.d("ibm", "onResponse: " + response.code());
                    // Handle the error here
                }
            }

            @Override
            public void onFailure(@NonNull Call<VolumeListResponse> call, @NonNull Throwable t) {
                // Handle the failure here
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void clearFavorites() {
        books.clear();
        books.addAll(previous);
        favoriesActive = false;
        bookAdapter.notifyDataSetChanged();
    }
}
