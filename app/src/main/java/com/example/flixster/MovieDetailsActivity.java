package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    // the movie to display
    Movie movie;

    // the view objects
    TextView tvTitle, tvOverview;
    RatingBar rbVoteAverage;
    ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        // using view binding instead
        //setContentView(R.layout.activity_movie_details);
//        // resolve the view objects
//        tvTitle = findViewById(R.id.tvTitle);
//        tvOverview = findViewById(R.id.tvOverview);
//        rbVoteAverage = findViewById(R.id.rbVoteAverage);

        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s", movie.getTitle()));

        String TMDB_API = this.getString(R.string.tmdb_api_key);
        String movie_url = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=" + TMDB_API;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(movie_url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d("Youtube API call", "onSuccess");
                JSONObject jsonObject = json.jsonObject;

                try {
                    final String ytKey = jsonObject.getJSONArray("results").getJSONObject(0).getString("key");

                    binding.ivPoster.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // intent needed to go to MovieTrailerActivity

                            Intent i = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                            i.putExtra("youtubeKey", ytKey);
                            startActivity(i);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("Youtube API call", "onFailure");
            }
        });



//        // set the title and overview
//        tvTitle.setText(movie.getTitle());
//        tvOverview.setText(movie.getOverview());
        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverview());

        // place poster image
        String imageUrl = movie.getBackdropPath();
        int placeHolder = R.drawable.flicks_backdrop_placeholder;
        Glide.with(this).load(imageUrl).placeholder(R.drawable.flicks_backdrop_placeholder).apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).into(binding.ivPoster);

        // vote average is 0-10, convert to 0-5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);


    }
}