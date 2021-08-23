package com.intelisoft.easytorrents.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.adapter.MovieListAdapter;
import com.intelisoft.easytorrents.adapter.RecyclerAdapter;
import com.intelisoft.easytorrents.iface.MovieLoaderListener;
import com.intelisoft.easytorrents.iface.MovieObserver;
import com.intelisoft.easytorrents.io.MovieLoader;
import com.intelisoft.easytorrents.model.Movie;
import com.intelisoft.easytorrents.util.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieFragment extends Fragment implements MovieLoaderListener,
        SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener, MovieObserver {
    //private MovieListAdapter adapter;
    private RecyclerAdapter adapter;
    private static MovieFragment _instance;
    private final String TAG = "MovieFragment";
    ProgressDialog pd;
    private LinearLayoutManager mLinearLayoutManager;


    @BindView(R.id.recycleView) RecyclerView recyclerView;
    @BindView(R.id.refresh) SwipeRefreshLayout layout;


    public MovieFragment() {
        Log.i(TAG, "Constructor");
    }

    public static MovieFragment getInstance() {
        if (_instance == null)
            _instance = new MovieFragment();
        return _instance;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!Utils.isConnectivityAvailable(getContext())) {
            Toast.makeText(getContext(), "No internet connectivity found. Its needed.", Toast.LENGTH_LONG).show();
        }
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading movies. Please wait.");
        pd.setProgressDrawable(getResources().getDrawable(R.drawable.progress));
        if (MovieLoader.getInstance(getActivity()).getMovies().size() <= 0)
            pd.show();

        //adapter = new MovieListAdapter(getView().getContext(), this, true);
        adapter = new RecyclerAdapter(getActivity(), true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration( new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        MovieLoader.getInstance(getActivity()).register(this);
    }


    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, getActivity());

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);

        if (layout != null) {
            layout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
            layout.setOnRefreshListener(this);
        }
    }
    public synchronized void setMovies(final ArrayList<Movie> movies) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = (RecyclerAdapter) recyclerView.getAdapter();
                    adapter.setMovies(movies);
                    adapter.notifyDataSetChanged();
                    recyclerView.invalidate();
                    recyclerView.requestLayout();
                }
            });
        }

        Log.i("Adapter", "setting Movies: " +movies.size());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onReloadMovies() {
        Log.i("MovieFagment", "Reloading from searver");
    }

    @Override
    public void onRefresh() {
        adapter.notifyDataSetChanged();
        layout.setRefreshing(false);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        MovieListAdapter adapter = (MovieListAdapter)adapterView.getAdapter();
        Movie m = adapter.getItem(position);
        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
        intent.putExtra("movie", m);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(MovieFragment.this.getActivity(), view, "profile");
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }


    public RecyclerAdapter getAdapter() {
        return adapter;
    }


    @Override
    public void update() {
        Log.i(TAG, "CALLED");
        pd.hide();
        MovieLoader.getInstance().remove(this);
    }


}
