package com.intelisoft.easytorrents.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.adapter.CastAdapter;
import com.intelisoft.easytorrents.adapter.TechSpecAdapter;
import com.intelisoft.easytorrents.io.MovieLoader;
import com.intelisoft.easytorrents.io.VolleySingleton;
import com.intelisoft.easytorrents.model.Movie;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.bg_image) NetworkImageView imageView;
    @BindView(R.id.adView)   AdView mAdView;
    @BindView(R.id.cv_cast)  CardView castCardView;
    @BindView(R.id.torrent_download_fab) FloatingActionButton fab;
    @BindView(R.id.screenshot1) NetworkImageView screenshot1;
    @BindView(R.id.screenshot2) NetworkImageView screenshot2;
    @BindView(R.id.screenshot3) NetworkImageView screenshot3;
    @BindView(R.id.relatedMovie1) NetworkImageView relatedMovie1;
    @BindView(R.id.relatedMovie2) NetworkImageView relatedMovie2;
    @BindView(R.id.relatedMovie3) NetworkImageView relatedMovie3;
    @BindView(R.id.scroll) NestedScrollView nestedScrollView;
    @BindView(R.id.tv_similar_movie1_title)  TextView tvRelatedMovie1;
    @BindView(R.id.tv_similar_movie2_title)  TextView tvRelatedMovie2;
    @BindView(R.id.tv_similar_movie3_title)  TextView tvRelatedMovie3;

    Palette.Swatch swatch;
    private final String TAG = "MovieDetailActivity";
    private Movie movie;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        TextView yearTextView   = (TextView) findViewById(R.id.movie_detail_year),
                 genreTextView  = (TextView) findViewById(R.id.movie_detail_genres),
                 ratingTextView =(TextView) findViewById(R.id.movie_detail_rating),
                 synopsisTextView=(TextView) findViewById(R.id.movie_detail_description);
        mAdView.setVisibility(View.GONE);

        ListView castList = (ListView) findViewById(R.id.listViewCast),
                torrentList=(ListView) findViewById(R.id.listview_torrent_details);
        ImageLoader mImageLoader = VolleySingleton.getInstance(this).getImageLoader();
        movie = (Movie) getIntent().getSerializableExtra("movie");
        setTitle(movie.getTitle());
        fab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        try {
            mImageLoader.get(movie.getLargeCoverImageUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG, "Failed to load image: " + error.toString());
                }
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Bitmap bitmap = response.getBitmap();
                    if (bitmap != null)
                        setToolbarColor(bitmap);
                }
            });
            Log.i(TAG, "ImageURL: " + movie.getLargeCoverImageUrl());
            imageView.setImageUrl(movie.getLargeCoverImageUrl(), mImageLoader);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, e.getMessage());
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        // Assignment
        yearTextView.setText(movie.getYear());
        genreTextView.setText(movie.getGenres().replace(",", " /"));
        ratingTextView.setText(movie.getRating() + " / 10");
        synopsisTextView.setText(movie.getSummary());
        screenshot1.setImageUrl(movie.getScreenshot1URL(),  mImageLoader);
        screenshot2.setImageUrl(movie.getScreenshot2URL(),  mImageLoader);
        screenshot3.setImageUrl(movie.getScreenshot3URL(),  mImageLoader);

        ///////////////////////
        if (movie.getCast().isEmpty()) {
            castCardView.setVisibility(View.GONE);
        } else  {
            castList.setAdapter( new CastAdapter(this, movie));
        }

        torrentList.setAdapter( new TechSpecAdapter(this, movie));
        torrentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TechSpecAdapter adapter = (TechSpecAdapter)adapterView.getAdapter();
                Intent intent  = new Intent(MovieDetailActivity.this, AddTorrentActivity.class);
                intent.putExtra("torrent", adapter.getItem(i));
                if (swatch != null)
                    intent.putExtra("swatchRgb", swatch.getRgb());
                startActivity(intent);
                Log.i("MovieDetail", adapter.getItem(i).getUrl());
            }
        });

        //////////////////////////
        ArrayList<Movie> suggestedMovies = movie.getSuggestedMovies();
        NetworkImageView[] views = new  NetworkImageView[suggestedMovies.size()];
        if (views.length == 0) {
            //No similar movies loaded
            return;
        }
        views[0] = relatedMovie1;
        views[1] = relatedMovie2;
        views[2] = relatedMovie3;

        ArrayList<Movie> suggestedNew = new ArrayList<>();
        for (int i = 0; i< suggestedMovies.size(); i++) {
            Movie m = suggestedMovies.get(i);
            try {
                mImageLoader.get(movie.getMediumCoverImageUrl(), ImageLoader.getImageListener(views[i],
                        R.mipmap.ic_launcher, android.R.drawable
                                .ic_dialog_alert));
                views[i].setImageUrl(m.getMediumCoverImageUrl(), mImageLoader);
                //MovieLoader.getInstance().load
                MovieLoader.getInstance().loadSuggestedMovies(m);
                suggestedNew.add(m);
                String title = (m.getTitle() + String.format(" (%s) ", m.getYear()));
                switch (i) {
                    case 0x0: tvRelatedMovie1.setText(title); break;
                    case 0x1: tvRelatedMovie2.setText(title); break;
                    case 0x2: tvRelatedMovie3.setText(title); break;
                }
            }catch (Exception e) {
               // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }

        }
        setupAds();
       // movie.setSuggestedMovies(suggestedNew);

    }

    private void setupAds() {
        MobileAds.initialize(this, getString(R.string.app_id));
        //.addTestDevice("A2FCAA2FF3DF27D67DD63E355C12BFFA")
        AdRequest adRequest = new AdRequest.Builder()/*.addTestDevice("A2FCAA2FF3DF27D67DD63E355C12BFFA")*/.build();
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
                super.onAdLoaded();
            }
        });
        mAdView.loadAd(adRequest);
    }

    private void collapseToolbar() {
        nestedScrollView.scrollTo(0, nestedScrollView.getBottom());
    }

    @OnClick (R.id.torrent_download_fab)
    void click () {
        //TODO:
        // Scroll view to download section
        //nestedScrollView.smoothScrollTo(0, nestedScrollView.getMaxScrollAmount ());
        collapseToolbar();
    }


    //This is extremely terrible
    @OnClick(R.id.relatedMovie1)
    public void relatedMovieClick1() {
        loadSimilarMovie(movie.getSuggestedMovies().get(0x0));
    }
    @OnClick(R.id.relatedMovie2)
    public void relatedMovieClick2() {
        loadSimilarMovie (movie.getSuggestedMovies().get(0x1));
    }
    @OnClick(R.id.relatedMovie3)
    public void relatedMovieClick3() {
        loadSimilarMovie (movie.getSuggestedMovies().get(0x2));
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void loadSimilarMovie(Movie m) {
        Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
        animation1.setDuration(900);

        Intent intent = new Intent(MovieDetailActivity.this, MovieDetailActivity.class);
        intent.putExtra("movie", m);
        //intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        NetworkImageView ivProfile = (NetworkImageView)findViewById(R.id.relatedMovie1);
        // ivProfile.startAnimation(animation1);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, ivProfile, "transition");
        startActivity(intent, options.toBundle());
        //finish();
    }

    // Return a palette's vibrant swatch after checking that it exists
    private Palette.Swatch checkVibrantSwatch(Palette p) {
        Palette.Swatch vibrant = p.getVibrantSwatch();
        return (vibrant != null) ? vibrant : null;
    }


    public void setToolbarColor(Bitmap bitmap) {
        Palette p =  Palette.from(bitmap).generate();
        Palette.Swatch vibrantSwatch = checkVibrantSwatch(p);
        if (vibrantSwatch != null) {
            swatch = vibrantSwatch;
            //TODO: Uncomment to set actionbar color to movie color
            // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(vibrantSwatch.getRgb()));
            fab.setBackgroundTintList(ColorStateList.valueOf(vibrantSwatch.getRgb()));
            collapsingToolbarLayout.setCollapsedTitleTextColor(vibrantSwatch.getRgb());
            collapsingToolbarLayout.setBackgroundColor(vibrantSwatch.getRgb());
            collapsingToolbarLayout.setContentScrimColor(vibrantSwatch.getTitleTextColor());
            collapsingToolbarLayout.setStatusBarScrimColor(vibrantSwatch.getRgb());


        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
