package com.example.googlebooks.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.googlebooks.R;
import com.example.googlebooks.model.Book;
import com.example.googlebooks.model.ImageLinks;
import com.example.googlebooks.model.interfaces.GoogleBooksApi;
import com.example.googlebooks.network.ApiClient;
import com.example.googlebooks.ui.activities.MainActivity;

import io.github.muddz.styleabletoast.StyleableToast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsFragment extends Fragment {
    private Book book;
    private String accessToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        book = requireArguments().getParcelable("book");
        accessToken = requireArguments().getString("token");

        TextView back = view.findViewById(R.id.back);
        TextView favorites = view.findViewById(R.id.favorites);
        ImageView imageView = view.findViewById(R.id.img);
        TextView description = view.findViewById(R.id.description);
        TextView language = view.findViewById(R.id.language);
        TextView pageCount = view.findViewById(R.id.pageCount);
        TextView publishedDate = view.findViewById(R.id.publishedDate);

        assert book != null;
        if (book.getVolumeInfo().getDescription() == null) {
            description.setText(R.string.el_libro_no_cuenta_con_descripci_n);
            description.setGravity(Gravity.CENTER);
        } else {
            description.setText(HtmlCompat.fromHtml(book.getVolumeInfo().getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        }
        language.append(("\t" + book.getVolumeInfo().getLanguage()));
        pageCount.append(("\t" + (book.getVolumeInfo().getPageCount())));
        publishedDate.append("\t" + (book.getVolumeInfo().getPublishedDate()));

        ImageLinks imageLinks = book.getVolumeInfo().getImageLinks();

        if (imageLinks != null) {
            String imageUrl = book.getVolumeInfo().getImageLinks().getThumbnail();
            Glide.with(requireContext())
                    .load(imageUrl) // Path to the local file
                    .override(200, 200) // Set the target dimensions
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_unknown_foreground);
        }

        // Regresar a la pantalla de búsqueda
        back.setOnClickListener(v -> {
            MainActivity.progress.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        // Agregar a favoritos
        favorites.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Agregar a favoritos")
                    .setMessage("¿Desea agregar el libro a favoritos?")
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Agregar", (dialog, which) -> {
                        addToFavorites();
                    })
                    .setCancelable(false)
                    .create().show();
        });
    }

    private void addToFavorites() {
        Log.d("ibm", "onResponse: " + book.getId());
        Log.d("ibm", "onResponse: " + accessToken);
        Call<Void> call = ApiClient.getClient()
                .create(GoogleBooksApi.class)
                .addToFavorites(0, book.getId(), "Bearer " + accessToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ibm", "onResponse: " + response.code());
                    new StyleableToast.Builder(requireContext())
                            .text("Agregado a favoritos")
                            .backgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                            .textColor(ContextCompat.getColor(requireContext(), R.color.white))
                            .show();
                    // The volume was successfully added to the favorites
                } else {
                    Log.d("ibm", "onResponse: " + response.code());
                    Log.d("ibm", "onResponse: " + response.errorBody());
                    Log.d("ibm", "onResponse: " + response.message());
                    // Handle the error
                    new StyleableToast.Builder(requireContext())
                            .text("Error al agregar volúmen")
                            .backgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                            .textColor(ContextCompat.getColor(requireContext(), R.color.white))
                            .show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                // Handle the failure
            }
        });
    }
}
