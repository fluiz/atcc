package com.arctouch.codechallenge.content.movie;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.MovieService;
import com.arctouch.codechallenge.interfaces.MovieCallbackInterface;
import com.arctouch.codechallenge.model.Movie;
import com.arctouch.codechallenge.util.Constants;
import com.arctouch.codechallenge.util.MovieImageUrlBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MovieActivity extends Activity {

    private static final String TAG = MovieActivity.class.getSimpleName();
    private int movieId;

    private final MovieImageUrlBuilder movieImageUrlBuilder = new MovieImageUrlBuilder();

    private TextView titleTextView;
    private TextView genresTextView;
    private TextView releaseDateTextView;
    private TextView overviewTextView;
    private ImageView posterImageView;
    private ImageView backdropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        titleTextView = findViewById(R.id.titleTextView);
        genresTextView = findViewById(R.id.genresTextView);
        releaseDateTextView = findViewById(R.id.releaseDateTextView);
        overviewTextView = findViewById(R.id.overviewTextView);
        posterImageView = findViewById(R.id.posterImageView);
        backdropImageView = findViewById(R.id.backdropImageView);

        try {
            Bundle bundle = getIntent().getExtras();
            movieId = bundle.getInt(Constants.MOVIE_ID_KEY);
        } catch (Exception e) {
            Log.w(TAG, "onCreate: no valid movie Id given", e);
            movieId = 0;
        }

        if(movieId > 0){
            Activity thisActivity = this;
            MovieService.getMovie(movieId, movie -> {
                titleTextView.setText(movie.title);
                genresTextView.setText(TextUtils.join(", ", movie.genres));
                releaseDateTextView.setText(movie.releaseDate);
                overviewTextView.setText(movie.overview);
                String posterPath = movie.posterPath;
                String backdropPath = movie.backdropPath;

                if (!TextUtils.isEmpty(posterPath)) {
                    Glide.with(thisActivity)
                            .load(movieImageUrlBuilder.buildPosterUrl(posterPath))
                            .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                            .into(posterImageView);
                }

                if (!TextUtils.isEmpty(backdropPath)) {
                    Glide.with(thisActivity)
                            .load(movieImageUrlBuilder.buildBackdropUrl(backdropPath))
                            .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                            .into(backdropImageView);
                }
            });
        }
    }
}
