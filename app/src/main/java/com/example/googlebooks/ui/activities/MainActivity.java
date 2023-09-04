package com.example.googlebooks.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.googlebooks.R;
import com.example.googlebooks.model.classes.AccessTokenResponse;
import com.example.googlebooks.model.classes.Bookshelf;
import com.example.googlebooks.model.classes.BookshelvesResponse;
import com.example.googlebooks.model.interfaces.GoogleBooksApi;
import com.example.googlebooks.network.ApiClient;
import com.example.googlebooks.network.GoogleOAuthService;
import com.example.googlebooks.ui.fragments.BookListFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final String CLIENT_ID = "185540064241-bh07pth20bs0ngr0mpekudgip8spg4sl.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String BASE_URL = "https://oauth2.googleapis.com/";
    private static final String SCOPE_URL = "https://www.googleapis.com/auth/books";
    public static AtomicBoolean favorites = new AtomicBoolean(false);

    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout progress;
    private ImageView profileImageView;
    private GoogleSignInClient googleSignInClient1;
    private String accessToken;
    private ArrayList<Bookshelf> bookshelves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = findViewById(R.id.progress);
        profileImageView = findViewById(R.id.profileImg);
        /*ImageView filter = findViewById(R.id.filter);*/

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CLIENT_ID)
                .requestServerAuthCode(CLIENT_ID, false)
                .requestScopes(new Scope(SCOPE_URL))
                .requestEmail()
                .build();

        googleSignInClient1 = GoogleSignIn.getClient(this, gso);

        // Iniciar el flujo de autenticación
        Intent signInIntent = googleSignInClient1.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

        // Filtrar por favoritos

        favorites.set(false);

        /*filter.setOnClickListener(v -> {
            if (favorites.get()) {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_sub_buttons);
                filter.setAnimation(animation);
                filter.setImageResource(R.drawable.heart_selector);
                BookListFragment.clearFavorites();
                favorites.set(false);
            } else {
                filter.setImageResource(R.drawable.ic_heart_red);
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_sub_buttons);
                filter.setAnimation(animation);
                BookListFragment.showFavorites(accessToken, context);
                favorites.set(true);
            }
        });*/

        //* Buscador de libros
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
            bundle.putString("token", accessToken);
            bundle.putParcelableArrayList("shelves", bookshelves);

            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                    .replace(R.id.container, BookListFragment.class, bundle).addToBackStack(null).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void getAccessToken(String authCode) throws IOException {
        //final String BASE_URL = "https://oauth2.googleapis.com/";

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        GoogleOAuthService oAuthService = retrofit.create(GoogleOAuthService.class);

        Call<AccessTokenResponse> call = oAuthService.exchangeCodeForToken(authCode, CLIENT_ID, CLIENT_SECRET,
                "authorization_code");

        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<AccessTokenResponse> call, @NonNull Response<AccessTokenResponse> response) {
                if (response.isSuccessful()) {
                    AccessTokenResponse tokenResponse = response.body();
                    assert tokenResponse != null;
                    accessToken = tokenResponse.getAccessToken();

                    Call<BookshelvesResponse> callShelves = ApiClient.getClient().create(GoogleBooksApi.class).getBookshelves("Bearer " + accessToken);

                    callShelves.enqueue(new Callback<BookshelvesResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<BookshelvesResponse> call, @NonNull Response<BookshelvesResponse> response) {
                            try {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "SUCCESS TOKEN", Toast.LENGTH_SHORT).show();
                                    BookshelvesResponse bookshelvesResponse = response.body();
                                    assert bookshelvesResponse != null;
                                    bookshelves = bookshelvesResponse.getItems();

                                } else {
                                    Toast.makeText(MainActivity.this, "ERROR TOKEN", Toast.LENGTH_SHORT).show();
                                    // Handle error
                                    Log.d("fav", "onResponse: error " + response.code());
                                }
                            } catch (Exception e) {
                                Log.e("fav", "onResponse: error " + e);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<BookshelvesResponse> call, @NonNull Throwable t) {
                            // Handle failure
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessTokenResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> result) {
        try {
            GoogleSignInAccount account = result.getResult(ApiException.class);
            Toast.makeText(this, "SUCESS", Toast.LENGTH_SHORT).show();
            updateUI(account);

            profileImageView.setOnClickListener(v -> {
                Log.d("lucy", "handleSignInResult: " + account);
                if (account != null) {
                    googleSignInClient1.signOut().addOnCompleteListener(this, task -> {
                        updateUI(null);
                    });
                }
            });

            String authCode = account.getServerAuthCode(); // Obtener el token de acceso
            getAccessToken(authCode);

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            if (account.getPhotoUrl() != null) {
                String photoUrl = account.getPhotoUrl().toString();
                RequestOptions requestOptions = new RequestOptions().transform(new RoundedCorners(300))
                        .placeholder(R.drawable.ic_user_foreground);
                Glide.with(this).load(photoUrl).apply(requestOptions).into(profileImageView);
            }
        } else {
            profileImageView.setImageResource(R.drawable.ic_user_foreground);
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient1.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}