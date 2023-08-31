package com.example.googlebooks.network;

import com.example.googlebooks.model.AccessTokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GoogleOAuthService {

    @POST("token")
    @FormUrlEncoded
    Call<AccessTokenResponse> exchangeCodeForToken(
            @Field("code") String code,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            //@Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType
    );

}