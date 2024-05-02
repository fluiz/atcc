package com.arctouch.codechallenge.home;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
    Activity thisActivity;
    private boolean searching = false;

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
                if (!searching) {
                    MovieService.getUpcomingMovies(desiredPage, moviesList -> {
                        if(moviesList.size() > 0){
                            Cache.addMovies(moviesList);
                            ((HomeAdapter)recyclerView.getAdapter()).appendData(moviesList);
                        }
                    });
                }
            }
        };

        try {
            if (savedInstanceState != null) {
                fetchCache = savedInstanceState.getBoolean(FETCH_CACHE_OPTION_KEY);
                esl.setCurrentPage(savedInstanceState.getInt(CURRENT_PAGE_KEY));
            }
            if (getIntent() != null) {
                handleIntent(getIntent());
            }
        } catch (Exception e) {
            Log.e(TAG, "No fetch cache option set", e);
        }

        thisActivity = this;

        if (fetchCache && !searching) {
            recyclerView.setAdapter(new HomeAdapter(thisActivity, Cache.getMovies()));
            progressBar.setVisibility(View.GONE);
        } else if (fetchCache && searching) {
            recyclerView.setAdapter(new HomeAdapter(thisActivity, Cache.getSearchResults()));
            progressBar.setVisibility(View.GONE);
        } else if (!searching) {
            esl.resetState();
            MovieService.getGenres(() -> loadServerData());
        }

        recyclerView.addOnScrollListener(esl);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        return true;
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

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()) && !searching) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searching = true;
            progressBar.setVisibility(View.VISIBLE);
            MovieService.searchMovie(query, moviesList -> {
                Cache.setSearchResults(moviesList);
                recyclerView.setAdapter(new HomeAdapter(thisActivity, moviesList));
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    public void onBackPressed() {
        if (searching) {
            searching = false;
            finish();
            overridePendingTransition(0, 0);
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(0, 0);
        }
    }

    private void loadServerData() {
        MovieService.getUpcomingMovies(1, moviesList -> {
            Cache.setMovies(moviesList);
            recyclerView.setAdapter(new HomeAdapter(thisActivity, moviesList));
            progressBar.setVisibility(View.GONE);
            fetchCache = true;
        });
    }
}
