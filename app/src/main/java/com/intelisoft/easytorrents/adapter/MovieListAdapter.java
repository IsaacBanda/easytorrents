package com.intelisoft.easytorrents.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.iface.MovieLoaderListener;
import com.intelisoft.easytorrents.iface.MovieObserver;
import com.intelisoft.easytorrents.iface.MovieSubject;
import com.intelisoft.easytorrents.io.MovieLoader;
import com.intelisoft.easytorrents.io.VolleySingleton;
import com.intelisoft.easytorrents.model.Movie;
import com.intelisoft.easytorrents.ui.MovieFragment;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Created by Technophile on 5/25/17.
 */

public class MovieListAdapter extends BaseAdapter  {
    private final Context ctx;
    private ArrayList<Movie> movies = new ArrayList<>();


    public MovieListAdapter(final Context context, MovieLoaderListener movieLoaderListener, boolean autoload) {
        this.ctx = context;
        if (autoload) {
            /**
             * Start a separate thread to handle data loading.
             * This ensures that the UI doesnt hang
             */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    movies = MovieLoader.getInstance(context).getMovies();
                }
            }).start();

        }
    }


    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        /*MovieFragment movieFragment = MovieFragment.getInstance();
        if (movieFragment.isProgressBarVisible()) {
            movieFragment.removeProgressBar();
        }*/
        View row;
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.movie_list_item, parent, false);
        } else {
            row = convertView;
        }

        // Lookup view for data population

        TextView tvName = (TextView) row.findViewById(R.id.title);
        TextView rating = (TextView) row.findViewById(R.id.rating);
        TextView genre  = (TextView) row.findViewById(R.id.genre);
        TextView year   = (TextView) row.findViewById(R.id.releaseYear);

        NetworkImageView imageView = (NetworkImageView) row.findViewById(R.id.thumbnail);

        tvName.setText(movie.getTitle());
        rating.setText(movie.getRating());
        genre.setText(movie.getGenres());
        year.setText(movie.getYear());

        ImageLoader mImageLoader = VolleySingleton.getInstance(row.getContext()).getImageLoader();
        mImageLoader.get(movie.getMediumCoverImageUrl(), ImageLoader.getImageListener(imageView,
                R.mipmap.ic_launcher, android.R.drawable
                        .ic_dialog_alert));
        imageView.setImageUrl(movie.getSmallCoverImageUrl(), mImageLoader);

        if (position  >= movies.size()-1) {
            Log.i("getView", "End");
            MovieLoader.getInstance().loadNextPage();
        }
        return row;
    }

    public synchronized void setMovies(ArrayDeque<Movie> movies) {
        for (Movie m : movies){
            if (!this.movies.contains(m)) {
                this.movies.add(m);
            }
        }

        //this.movies = movies;
        //notifyDataSetChanged();

    }

    public synchronized void setMovies(ArrayList<Movie> movies) {
        for (Movie m : movies){
            if (!this.movies.contains(m)) {
                this.movies.add(m);
            }
        }

        //this.movies = movies;
        //notifyDataSetChanged();

    }



}
