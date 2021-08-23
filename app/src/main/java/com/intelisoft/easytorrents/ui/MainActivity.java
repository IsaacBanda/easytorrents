package com.intelisoft.easytorrents.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.animation.DecelerateInterpolator;

import com.astuetz.PagerSlidingTabStrip;
import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.adapter.SearchSuggestionAdapter;
import com.intelisoft.easytorrents.io.Config;
import com.intelisoft.easytorrents.io.FileSystemIO;
import com.intelisoft.easytorrents.io.MovieLoader;
import com.intelisoft.easytorrents.model.Movie;


import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import im.delight.apprater.AppRater;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnSuggestionListener, SearchView.OnQueryTextListener,
        SearchView.OnCloseListener {
    private   SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(enterTransition());
            getWindow().setSharedElementReturnTransition(returnTransition());
        }
        setContentView(R.layout.activity_main);

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pager.setAdapter(new TestAdapter(getSupportFragmentManager()));
                        tabs.setViewPager(pager);
                    }
                });
            }
        }).start();

        try {
            setup();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppRater appRater = new AppRater(this);
        appRater.setDaysBeforePrompt(3);
        appRater.setLaunchesBeforePrompt(7);

        appRater.setPhrases("Rate this app", "We hope you enjoy this app. Kindly rate us.", "Rate now", "Later", "No, thanks");
        appRater.setTargetUri(getString(R.string.store_url));
        appRater.setPreferenceKeys("app_rater", "flag_dont_show", "launch_count", "first_launch_time");

        appRater.show();

    }

    private void setup() throws JSONException {
        FileSystemIO.setContext(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnSuggestionListener(this);
            searchView.setOnQueryTextListener(this);
            searchView.setIconifiedByDefault(true);
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();

            searchView.setSuggestionsAdapter(new SearchSuggestionAdapter(this));
            //searchView.setDrop(getResources().getColor(R.color.tabTitlePrimary));
        }

        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (query != null && !query.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
            intent.putExtra("searchQuery", query);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText != null && !newText.isEmpty() && newText.length()>2) {
            //MovieLoader.getInstance(this).clearSearchResults();
            MovieLoader.getInstance(this).search(newText);
            ArrayList<Movie> movies = MovieLoader.getInstance(this).getSearchedMovies();
            Log.i("Seach results: " , movies.size()+"");
        }

        return true;
    }

    @Override
    public boolean onClose() {
        MovieFragment.getInstance().setMovies(MovieLoader.getInstance(this).getMovies());
        return true;
    }

    @Override
    protected void onDestroy() {
        //Save any running downloads
       Config config  = Config.getInstance();
        try {
            config.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Transition enterTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(5);

        return bounds;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Transition returnTransition() {
        ChangeBounds bounds = new ChangeBounds();
        bounds.setInterpolator(new DecelerateInterpolator());
        bounds.setDuration(5);

        return bounds;
    }
}
