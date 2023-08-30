package com.example.googlebooks.ui.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.googlebooks.R;
import com.example.googlebooks.model.Book;
import com.example.googlebooks.model.ImageLinks;
import com.example.googlebooks.ui.activities.MainActivity;

public class DetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.book_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Book book = requireArguments().getParcelable("book");

        TextView back = view.findViewById(R.id.back);
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

        back.setOnClickListener(v -> {
            MainActivity.progress.setVisibility(View.VISIBLE);
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });
    }
}