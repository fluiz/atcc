package com.arctouch.codechallenge.interfaces;

import com.arctouch.codechallenge.model.Movie;
import java.util.List;

/**
 * Created by Fabio on 07/03/18.
 */

public interface MoviesListCallbackInterface {
    void onGetMoviesListSuccess(List<Movie> moviesList);
}
