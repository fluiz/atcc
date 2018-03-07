package com.arctouch.codechallenge.api;

import android.view.View;

import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.home.HomeAdapter;
import com.arctouch.codechallenge.interfaces.GenresCallbackInterface;
import com.arctouch.codechallenge.interfaces.MovieCallbackInterface;
import com.arctouch.codechallenge.interfaces.UpcomingMoviesCallbackInterface;
import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.model.UpcomingMoviesResponse;
import com.arctouch.codechallenge.util.Constants;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Created by Fabio on 06/03/18.
 */

public class MovieService {

    private static final TmdbApi api = new Retrofit.Builder()
            .baseUrl(Constants.URL)
            .client(new OkHttpClient.Builder().build())
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
