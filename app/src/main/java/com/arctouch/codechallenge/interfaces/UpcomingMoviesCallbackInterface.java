package com.arctouch.codechallenge.interfaces;

import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.model.UpcomingMoviesResponse;

import java.util.List;

/**
 * Created by Fabio on 07/03/18.
 */

public interface UpcomingMoviesCallbackInterface {
    void onGetUpcomingMoviesSuccess(List<Movie> moviesList);
}
