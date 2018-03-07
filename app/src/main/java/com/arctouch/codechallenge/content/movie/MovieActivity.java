package com.arctouch.codechallenge.content.movie;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.arctouch.codechallenge.R;
import com.arctouch.codechallenge.api.MovieService;
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
        }

        MovieService.api.movie((long) movieId, Constants.API_KEY, Constants.DEFAULT_LANGUAGE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    titleTextView.setText(response.title);
                    genresTextView.setText(TextUtils.join(", ", response.genres));
                    releaseDateTextView.setText(response.releaseDate);
                    overviewTextView.setText(response.overview);
                    String posterPath = response.posterPath;
                    String backdropPath = response.backdropPath;

                    if (!TextUtils.isEmpty(posterPath)) {
                        Glide.with(this)
                                .load(movieImageUrlBuilder.buildPosterUrl(posterPath))
                                .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                                .into(posterImageView);
                    }

                    if (!TextUtils.isEmpty(backdropPath)) {
                        Glide.with(this)
                                .load(movieImageUrlBuilder.buildBackdropUrl(backdropPath))
                                .apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                                .into(backdropImageView);
                    }
                });
    }
}
