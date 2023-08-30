package com.example.googlebooks.ui.activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.googlebooks.R;
import com.example.googlebooks.model.ApiResponse;
import com.example.googlebooks.model.interfaces.Bookshelf;
import com.example.googlebooks.model.interfaces.BookshelvesResponse;
import com.example.googlebooks.model.interfaces.GoogleBooksApi;
import com.example.googlebooks.network.ApiClient;
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
import com.google.android.gms.tasks.Task;

import java.net.ConnectException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout progress;
    LinearLayout header;
    //private GoogleApi googleApiClient;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progress = findViewById(R.id.progress);

        //TODO
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("185540064241-60r8nib88uqikc3ove20authegbddfrk.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /*googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/

        // Iniciar el flujo de autenticación
        signIn();

        header = findViewById(R.id.header);
        header.setOnClickListener(v -> {
            Call<BookshelvesResponse> call = ApiClient.getClient().create(GoogleBooksApi.class)
                    .getBookshelves("Bearer " + "185540064241-9o2l86dbpksesnthu62e2j69ko3b38o5");

            call.enqueue(new Callback<BookshelvesResponse>() {
                @Override
                public void onResponse(@NonNull Call<BookshelvesResponse> call, Response<BookshelvesResponse> response) {
                    if (response.isSuccessful()) {
                        BookshelvesResponse bookshelvesResponse = response.body();
                        List<Bookshelf> bookshelves = bookshelvesResponse.getItems();

                        Log.d("fav", "onResponse: ");
                        // Handle the list of bookshelves
                    } else {
                        // Handle error
                        Log.d("fav", "onResponse: error ");
                    }
                }

                @Override
                public void onFailure(Call<BookshelvesResponse> call, Throwable t) {
                    // Handle failure
                }
            });
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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> result) {

        try {
            GoogleSignInAccount account = result.getResult(ApiException.class);
            Toast.makeText(this, "SUCESS", Toast.LENGTH_SHORT).show();
            //GoogleSignInAccount account = result.get;
            String idToken = account.getIdToken(); // Obtener el token de acceso
            Log.d("fav", "handleSignInResult: " + idToken);
        } catch (ApiException e) {
            //e.printStackTrace();
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            Log.e("fav", "handleSignInResult: " + e.getStatus());
            Log.e("fav", "handleSignInResult: " + e.getStatusCode());
            Log.e("fav", "handleSignInResult: " + e.getStackTrace().toString());
            Log.e("fav", "handleSignInResult: " + e.getCause());
            Log.e("fav", "handleSignInResult: " + e.getMessage());
        }
    }
}