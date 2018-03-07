package com.arctouch.codechallenge.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.MovieService;
import com.arctouch.codechallenge.content.movie.MovieActivity;
import com.arctouch.codechallenge.interfaces.UpcomingMoviesCallbackInterface;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.Constants;
import com.arctouch.codechallenge.util.EndlessScrollListener;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EndlessScrollListener esl;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        this.recyclerView = findViewById(R.id.recyclerView);
        this.progressBar = findViewById(R.id.progressBar);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        Activity thisActivity = this;

        MovieService.getGenres(() -> MovieService.getUpcomingMovies(1, moviesList -> {
            recyclerView.setAdapter(new HomeAdapter(thisActivity, moviesList));
            progressBar.setVisibility(View.GONE);
        }));

        esl = new EndlessScrollListener(llm) {
            @Override
            public void loadMoreItems(int desiredPage) {
                MovieService.getUpcomingMovies(desiredPage, moviesList -> ((HomeAdapter)recyclerView.getAdapter()).appendData(moviesList));
            }
        };
        recyclerView.addOnScrollListener(esl);
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
