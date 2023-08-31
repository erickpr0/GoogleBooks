package com.example.googlebooks.ui.activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.googlebooks.R;
import com.example.googlebooks.model.AccessTokenResponse;
import com.example.googlebooks.model.ApiResponse;
import com.example.googlebooks.model.interfaces.Bookshelf;
import com.example.googlebooks.model.interfaces.BookshelvesResponse;
import com.example.googlebooks.model.interfaces.GoogleBooksApi;
import com.example.googlebooks.network.ApiClient;
import com.example.googlebooks.network.GoogleOAuthService;
import com.example.googlebooks.ui.fragments.BookListFragment;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout progress;
    LinearLayout header;
    //private GoogleApi googleApiClient;
    private GoogleSignInClient googleSignInClient;
    private String idToken;
    private WebView webView;

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = findViewById(R.id.progress);

        //TODO
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("185540064241-bh07pth20bs0ngr0mpekudgip8spg4sl.apps.googleusercontent.com")
                .requestServerAuthCode("185540064241-bh07pth20bs0ngr0mpekudgip8spg4sl.apps.googleusercontent.com", false)
                .requestScopes(new Scope("https://www.googleapis.com/auth/books"))
                .requestEmail()
                .build();


        GoogleSignInClient googleSignInClient1 = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = googleSignInClient1.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        // Iniciar el flujo de autenticación
        //signIn();

        header = findViewById(R.id.header);
        header.setOnClickListener(v -> {

        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void sendToken(String authCode) throws IOException {

        final String BASE_URL = "https://oauth2.googleapis.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleOAuthService oAuthService = retrofit.create(GoogleOAuthService.class);

        Call<AccessTokenResponse> call = oAuthService.exchangeCodeForToken(
                authCode,
                "185540064241-bh07pth20bs0ngr0mpekudgip8spg4sl.apps.googleusercontent.com",
                "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",
                //"https://com.example.googlebooks",
                "authorization_code"
        );

        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                Log.e("lucy", "onResponse: " + response.code());
                Log.e("lucy", "onResponse: " + response.message());
                Log.e("lucy", "onResponse: " + response.errorBody());
                Log.e("lucy", "onResponse: " + response.body());
                Log.e("lucy", "onResponse: " + response.raw());
                if (response.isSuccessful()) {
                    AccessTokenResponse tokenResponse = response.body();
                    assert tokenResponse != null;
                    String accessToken = tokenResponse.getAccessToken();
                    Log.d("lucy", "onResponse: accesToken: " + accessToken);
                    // Hacer algo con el token de acceso

                    //TODO
                    Call<BookshelvesResponse> callShelves = ApiClient.getClient()
                            .create(GoogleBooksApi.class)
                            .getBookshelves("Bearer " + accessToken);

                    callShelves.enqueue(new Callback<BookshelvesResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<BookshelvesResponse> call, @NonNull Response<BookshelvesResponse> response) {
                            try {
                                if (response.isSuccessful()) {
                                    Log.d("lucy", "onResponse: successfully ");
                                    Toast.makeText(MainActivity.this, "SUCCESS TOKEN", Toast.LENGTH_SHORT).show();
                                    BookshelvesResponse bookshelvesResponse = response.body();
                                    List<Bookshelf> bookshelves = bookshelvesResponse.getItems();

                                    for (Bookshelf b : bookshelves) {
                                        Log.d("lucy", "onResponse: bookshelve " + b.getTitle());
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "ERROR TOKEN", Toast.LENGTH_SHORT).show();
                                    // Handle error
                                    Log.d("fav", "onResponse: error " + response.code());
                                    Log.d("fav", "onResponse: error " + response.message());
                                    Log.d("fav", "onResponse: error " + response.errorBody().toString());
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

                } else {
                    Log.d("lucy", "onResponse: error");
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {

            }
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> result) {

        try {
            GoogleSignInAccount account = result.getResult(ApiException.class);
            Toast.makeText(this, "SUCESS", Toast.LENGTH_SHORT).show();
            String authCode = account.getServerAuthCode(); // Obtener el token de acceso

            Log.d("lucy", "handleSignInResult: " + account.getServerAuthCode());

            sendToken(authCode);

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}