package com.arctouch.codechallenge.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.MovieService;
import com.arctouch.codechallenge.content.movie.MovieActivity;
import com.arctouch.codechallenge.data.Cache;
import com.arctouch.codechallenge.util.Constants;
import com.arctouch.codechallenge.util.EndlessScrollListener;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private EndlessScrollListener esl;
    private ProgressBar progressBar;
    private boolean fetchCache = false;

    private final String FETCH_CACHE_OPTION_KEY = "fetch_cache";
    private final String CURRENT_PAGE_KEY = "current_page";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        this.recyclerView = findViewById(R.id.recyclerView);
        this.progressBar = findViewById(R.id.progressBar);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        esl = new EndlessScrollListener(llm) {
            @Override
            public void loadMoreItems(int desiredPage) {
                MovieService.getUpcomingMovies(desiredPage, moviesList -> {
                    if(moviesList.size() > 0){
                        Cache.addMovies(moviesList);
                        ((HomeAdapter)recyclerView.getAdapter()).appendData(moviesList);
                    }
                });
            }
        };

        try {
            if (savedInstanceState != null) {
                fetchCache = savedInstanceState.getBoolean(FETCH_CACHE_OPTION_KEY);
                esl.setCurrentPage(savedInstanceState.getInt(CURRENT_PAGE_KEY));
            }
        } catch (Exception e) {
            Log.e(TAG, "No fetch cache option set", e);
        }

        Activity thisActivity = this;

        if (fetchCache) {
            recyclerView.setAdapter(new HomeAdapter(thisActivity, Cache.getMovies()));
            progressBar.setVisibility(View.GONE);
        } else {
            esl.resetState();
            MovieService.getGenres(() -> MovieService.getUpcomingMovies(1, moviesList -> {
                Cache.setMovies(moviesList);
                recyclerView.setAdapter(new HomeAdapter(thisActivity, moviesList));
                progressBar.setVisibility(View.GONE);
                fetchCache = true;
            }));
        }

        recyclerView.addOnScrollListener(esl);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(FETCH_CACHE_OPTION_KEY, fetchCache);
        outState.putInt(CURRENT_PAGE_KEY, esl.getCurrentPage());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fetchCache = false;
        esl.resetState();
    }

    protected void showMovieDetails(int movieId) {
        Intent detailsIntent = new Intent(this, MovieActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.MOVIE_ID_KEY, movieId);
        detailsIntent.putExtras(bundle);
        startActivity(detailsIntent);
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
