package com.arctouch.codechallenge.interfaces;

import com.arctouch.codechallenge.model.Movie;

/**
 * Created by Fabio on 07/03/18.
 */

public interface MovieCallbackInterface {
    void onGetMovieSuccess(Movie movie);
}
