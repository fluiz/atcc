package com.arctouch.codechallenge.api;

import com.arctouch.codechallenge.model.GenreResponse;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.model.UpcomingMoviesResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApi {

    @GET("genre/movie/list")
    Observable<GenreResponse> genres();

    @GET("movie/upcoming")
    Observable<UpcomingMoviesResponse> upcomingMovies(
            @Query("page") Long page,
            @Query("region") String region
    );

    @GET("movie/{id}")
    Observable<Movie> movie(
            @Path("id") Long id
    );
}
