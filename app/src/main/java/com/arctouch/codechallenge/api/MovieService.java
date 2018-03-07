package com.arctouch.codechallenge.api;

import com.arctouch.codechallenge.util.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by Fabio on 06/03/18.
 */

public class MovieService {

    public static final TmdbApi api = new Retrofit.Builder()
            .baseUrl(Constants.URL)
            .client(new OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(TmdbApi.class);
}
