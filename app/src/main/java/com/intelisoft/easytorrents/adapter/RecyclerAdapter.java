package com.intelisoft.easytorrents.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.iface.MovieObserver;
import com.intelisoft.easytorrents.iface.MovieSubject;
import com.intelisoft.easytorrents.io.MovieLoader;
import com.intelisoft.easytorrents.io.VolleySingleton;
import com.intelisoft.easytorrents.model.Movie;
import com.intelisoft.easytorrents.ui.MainActivity;
import com.intelisoft.easytorrents.ui.MovieDetailActivity;
import com.intelisoft.easytorrents.ui.MovieFragment;

import java.util.ArrayList;

/**
 * Created by Technophile on 6/27/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MovieHolder>  {

    private static Activity ctx;
    private static final String TAG = "RecyclerAdapter";
    private static ArrayList<Movie> movies = new ArrayList<>();
    private static ArrayList<MovieObserver> movieObservers;

    public RecyclerAdapter (final Activity ctx, boolean autoload) {
        this.ctx = ctx;
        if (autoload) {
            /**
             * Start a separate thread to handle data loading.
             * This ensures that the UI doesnt hang
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    movies = MovieLoader.getInstance(ctx).getMovies();
                }
            }).start();
        }
        movieObservers = new ArrayList<>();
    }

    @Override
    public RecyclerAdapter.MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);
        return new MovieHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.MovieHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bindMovie(movie);
        if (position  >= movies.size()-1) {
            Log.i(TAG, "Scrolled to end. Loading next page");
            MovieLoader.getInstance().loadNextPage();
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public synchronized void setMovies(ArrayList<Movie> movies) {
        for (Movie m : movies){
            if (!this.movies.contains(m)) {
                this.movies.add(m);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // VIEW HOLDER CLASS
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        private TextView tvRating;
        private TextView tvGenre;
        private TextView tvYear;
        private NetworkImageView imageView ;
        private ImageLoader mImageLoader;

        public MovieHolder(View view) {
            super(view);

            tvName   = (TextView) view.findViewById(R.id.title);
            tvRating = (TextView) view.findViewById(R.id.rating);
            tvGenre  = (TextView) view.findViewById(R.id.genre);
            tvYear   = (TextView) view.findViewById(R.id.releaseYear);
            imageView = (NetworkImageView) view.findViewById(R.id.thumbnail);
            mImageLoader = VolleySingleton.getInstance(view.getContext()).getImageLoader();
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Movie movie = movies.get(getAdapterPosition());
            Intent intent = new Intent(ctx, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(ctx, view, "profile");
                ctx.startActivity(intent, options.toBundle());
            } else {
                ctx.startActivity(intent);
            }
            Log.i(TAG, "Clicked");
        }

        public void bindMovie(Movie movie) {
            tvName.setText(movie.getTitle());
            tvRating.setText(movie.getRating());
            tvGenre.setText(movie.getGenres());
            tvYear.setText(movie.getYear());;

            mImageLoader.get(movie.getMediumCoverImageUrl(), ImageLoader.getImageListener(imageView,
                    R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));
            imageView.setImageUrl(movie.getSmallCoverImageUrl(), mImageLoader);
        }
    }
}
