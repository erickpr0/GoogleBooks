package com.example.googlebooks.model.interfaces;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Bookshelf implements Parcelable {
    private final String id;
    private final String title;

    protected Bookshelf(Parcel in) {
        id = in.readString();
        title = in.readString();
    }

    public static final Creator<Bookshelf> CREATOR = new Creator<Bookshelf>() {
        @Override
        public Bookshelf createFromParcel(Parcel in) {
            return new Bookshelf(in);
        }

        @Override
        public Bookshelf[] newArray(int size) {
            return new Bookshelf[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
    }
}
