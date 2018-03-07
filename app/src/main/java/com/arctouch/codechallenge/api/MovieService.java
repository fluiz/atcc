package com.arctouch.codechallenge.api;

import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.interfaces.GenresCallbackInterface;
import com.arctouch.codechallenge.interfaces.MovieCallbackInterface;
import com.arctouch.codechallenge.interfaces.UpcomingMoviesCallbackInterface;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by Fabio on 06/03/18.
 */

public class MovieService {

    private static final OkHttpClient okHttpClient = new OkHttpClient()
            .newBuilder()
            .addInterceptor(chain -> {
                Request originalRequest = chain.request();
                HttpUrl originalUrl = originalRequest.url();

                HttpUrl url = originalUrl.newBuilder()
                        .addQueryParameter("api_key", Constants.API_KEY)
                        .addQueryParameter("language", Constants.DEFAULT_LANGUAGE)
                        .build();

                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
            ).build();

    private static final TmdbApi api = new Retrofit.Builder()
            .baseUrl(Constants.URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(TmdbApi.class);


    public static void getMovie(int movieId, MovieCallbackInterface mci) {
        api.movie((long) movieId, Constants.API_KEY, Constants.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieFound -> {
                    mci.onGetMovieSuccess(movieFound);
                });
    }

    public static void getUpcomingMovies(int page, UpcomingMoviesCallbackInterface umci) {
        api.upcomingMovies(Constants.API_KEY, Constants.DEFAULT_LANGUAGE, (long) page, Constants.DEFAULT_REGION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(moviesList -> {
                    for (Movie movie : moviesList.results) {
                        movie.genres = new ArrayList<>();
                        for (Genre genre : Cache.getGenres()) {
                            if (movie.genreIds.contains(genre.id)) {
                                movie.genres.add(genre);
                            }
                        }
                    }
                    umci.onGetUpcomingMoviesSuccess(moviesList.results);
                });
    }

    public static void getGenres(GenresCallbackInterface gci) {
        if(Cache.getGenres() != null && Cache.getGenres().size() > 0) {
            gci.onGetGenresSuccess();
        } else {
            api.genres(Constants.API_KEY, Constants.DEFAULT_LANGUAGE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Cache.setGenres(response.genres);
                        gci.onGetGenresSuccess();
                    });
        }
    }
}
