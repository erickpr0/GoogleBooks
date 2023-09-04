package com.example.googlebooks.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.googlebooks.R;
import com.example.googlebooks.model.classes.Book;
import com.example.googlebooks.model.classes.ImageLinks;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> books;
    private Context context;
    OnClickListener onClickListener;

    public BookAdapter() {
    }

    public BookAdapter(List<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("book", "onBindViewHolder: " + books.get(position).getVolumeInfo().getTitle());
        holder.tittle.setText(books.get(position).getVolumeInfo().getTitle());
        ImageLinks imageLinks = books.get(position).getVolumeInfo().getImageLinks();

        if (imageLinks != null) {
            String imageUrl = books.get(position).getVolumeInfo().getImageLinks().getThumbnail();
            Log.d("url", "onBindViewHolder: " + imageUrl);
            Glide.with(context)
                    .load(imageUrl)
                    .override(200, 200)
                    .into(holder.img);

        } else {
            holder.img.setImageResource(R.drawable.ic_unknown_foreground);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(position);
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void addBooks(List<Book> newBooks) {
        int startPosition = books.size();
        books.addAll(newBooks);
        notifyItemRangeInserted(startPosition, newBooks.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final TextView tittle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tittle = itemView.findViewById(R.id.title);
        }
    }
}
