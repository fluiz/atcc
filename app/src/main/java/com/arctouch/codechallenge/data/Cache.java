package com.arctouch.codechallenge.data;

import com.arctouch.codechallenge.model.Genre;
import com.arctouch.codechallenge.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class Cache {

    private static List<Genre> genres = new ArrayList<>();
    private static List<Movie> movies = new ArrayList<>();
    private static List<Movie> searchResults = new ArrayList<>();

    public static List<Genre> getGenres() {
        return genres;
    }

    public static void setGenres(List<Genre> genres) {
        Cache.genres.clear();
        Cache.genres.addAll(genres);
    }

    public static List<Movie> getMovies() { return movies; }

    public static void setMovies(List<Movie> movies) {
        Cache.movies.clear();
        Cache.movies.addAll(movies);
    }

    public static void addMovies(List<Movie> movies) {
        Cache.movies.addAll(movies);
    }

    public static List<Movie> getSearchResults() { return movies; }

    public static void setSearchResults(List<Movie> movies) {
        Cache.searchResults.clear();
        Cache.searchResults.addAll(movies);
    }
}
