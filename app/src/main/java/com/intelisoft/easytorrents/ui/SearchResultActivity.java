package com.intelisoft.easytorrents.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.toolbox.NetworkImageView;
import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.adapter.MovieListAdapter;
import com.intelisoft.easytorrents.iface.MovieLoaderListener;
import com.intelisoft.easytorrents.io.MovieLoader;
import com.intelisoft.easytorrents.io.SearchQueryCache;
import com.intelisoft.easytorrents.model.Movie;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchResultActivity extends AppCompatActivity implements MovieLoaderListener, AdapterView.OnItemClickListener {

    private ListView listView;
    private ProgressBar bar;
    private MovieListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        String query = getIntent().getStringExtra("searchQuery");
        setTitle(query + " search results");
        if (query.contains(" ")) query = URLEncoder.encode(query);

        listView = (ListView) findViewById(R.id.movie_search_list);
        bar = (ProgressBar) this.findViewById(R.id.progressBar);

        adapter = new MovieListAdapter(this, this, false);

        new Worker().execute(query);

        listView.setOnItemClickListener(this);


    }

    @Override
    public void onReloadMovies() {

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        MovieListAdapter adapter = (MovieListAdapter)adapterView.getAdapter();
        Movie m = adapter.getItem(position);
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", m);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "profile");
        startActivity(intent, options.toBundle());
    }

    class Worker extends AsyncTask<String, Void, Void> {
        private Context ctx;
        private MovieLoader mLoader;
        private String query;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ctx = getApplicationContext();
            mLoader = MovieLoader.getInstance(ctx);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            // Get search results from cache
            ArrayList<Movie> searchedMovies = SearchQueryCache.getInstance().get(query);
            if (searchedMovies.size() > 0) {
                adapter.setMovies(searchedMovies);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listView.invalidate();
                listView.requestLayout();
            } else {
                Log.i("Search", "No Results");
            }
            bar.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... voids) {
            //MovieLoader.getInstance(ctx).clearSearchResults();
            query = voids[0];
            SearchQueryCache cache = SearchQueryCache.getInstance();
            if (SearchQueryCache.getInstance().contains(query)){
                cancel(false);
            } else {
                Log.i("Search", "Cache Miss. Searching");
                MovieLoader.getInstance(ctx).search(query);

                for (int i = 0; i < Integer.MAX_VALUE / 2; i += 2)
                    if (i % 2 == 0) ;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /**
             * Wait for 5 seconds. if no search results, blah, blah
             */
            ArrayList<Movie> searchedMovies = MovieLoader.getInstance().getSearchedMovies();
            if (searchedMovies.size() > 0) {
                adapter.setMovies(searchedMovies);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listView.invalidate();
                listView.requestLayout();
            } else {
                Log.i("Search", "No Results");
            }
            bar.setVisibility(View.GONE);


        }
    }
}
