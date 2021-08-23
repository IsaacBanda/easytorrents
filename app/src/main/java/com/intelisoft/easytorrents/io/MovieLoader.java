package com.intelisoft.easytorrents.io;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.intelisoft.easytorrents.adapter.MovieListAdapter;
import com.intelisoft.easytorrents.adapter.RecyclerAdapter;
import com.intelisoft.easytorrents.iface.MovieObserver;
import com.intelisoft.easytorrents.iface.MovieSubject;
import com.intelisoft.easytorrents.model.Cast;
import com.intelisoft.easytorrents.model.Movie;
import com.intelisoft.easytorrents.ui.MovieFragment;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Technophile on 11/21/16.
 */

public class MovieLoader implements MovieSubject {

    private static MovieLoader __instance;
    private ArrayList<Movie> movies = new ArrayList<>();
    private ArrayList<Movie> searcheMovies = new ArrayList<>();
    private final String url = "https://yts.ag/api/v2/list_movies.json?page=%d";
    private final String MOVIE_DETAILS_URL = "https://yts.ag/api/v2/movie_details.json?movie_id=%d&with_images=true&with_cast=true";
    private final String MOVIE_SEARCH_URL = "https://yts.ag/api/v2/list_movies.json?query_term=%s";
    private final String MOVIE_SUGGESTIONS = "https://yts.ag/api/v2/movie_suggestions.json?movie_id=%s";

    private  RequestQueue requestQueue;
    private Context ctx;
    private ArrayList<MovieObserver> observers = new ArrayList<>();
    private  int currentPage = 1;
    private static boolean isLoading = false;
    private final String TAG = "MovieLoader";

    private ThreadPool threadPool;

    private MovieLoader(Context ctx) {
        this.ctx = ctx;
        threadPool = new ThreadPool(2, 2);
        loadNextPage();

    }

    private  Movie createMovie(JSONObject movie) throws JSONException {
        String genres[] = movie.get("genres").toString().split("\\,");
        ArrayList<Movie.Torrent> torrents = new ArrayList<>();
        JSONArray a = movie.getJSONArray("torrents");
        for (int i = 0; i < a.length(); i++) {
            JSONObject object = a.getJSONObject(i);
            torrents.add(new Movie.Torrent(movie.getString("title"), object.getString("url"), object.getString("quality"),
                    object.getString("size"), object.getString("date_uploaded"),
                    object.getString("seeds"), object.getString("peers"), movie.getString("runtime")));
        }
        Movie moviey = new Movie();
       // Log.i(TAG, "addMovie: \n" + movie.toString(2));
        moviey
                .setId((int) movie.get("id")).setUrl(movie.getString("url")).setTitle(movie.getString("title"))
                .setYear(movie.getString("year")).setRating(movie.getString("rating"))
                .setRuntime(movie.getString("runtime")).setGenres(genres).setSummary(movie.getString("summary"))
                .setDescriptionFull(movie.getString("description_full")).setLanguage(movie.getString("language"))
                .setBgImageURL(movie.getString("background_image")).setBgImageOriginalURL(movie.getString("background_image_original"))
                .setSmallCoverImageUrl(movie.getString("small_cover_image")).setMediumCoverImageUrl(movie.getString("medium_cover_image"))
                .setLargeCoverImageUrl(movie.getString("medium_cover_image")).setTorrents(torrents);

        loadMovieScreenshots(moviey);
        loadCast(moviey);
        return moviey;
    }

    /**
     * Construct a movie object from JSONObject
     *
     * @param movie
     * @throws JSONException
     */
    private  Movie addMovie(JSONObject movie, boolean isSearch) throws JSONException {
        //Log.i("addMovie: ", movie.toString(5));
        String genres[] = movie.get("genres").toString().split("\\,");
        ArrayList<Movie.Torrent> torrents = new ArrayList<>();
        JSONArray a = movie.getJSONArray("torrents");
        for (int i = 0; i < a.length(); i++) {
            JSONObject object = a.getJSONObject(i);
            torrents.add(new Movie.Torrent(movie.getString("title"), object.getString("url"), object.getString("quality"),
                    object.getString("size"), object.getString("date_uploaded"),
                    object.getString("seeds"), object.getString("peers"), movie.getString("runtime")));
        }
        //Log.i("addMovie", "Images Loaded");
        Movie moviey = new Movie();
        moviey
                .setId((int) movie.get("id")).setUrl(movie.getString("url")).setTitle(movie.getString("title"))
                .setYear(movie.getString("year")).setRating(movie.getString("rating"))
                .setRuntime(movie.getString("runtime")).setGenres(genres).setSummary(movie.getString("summary"))
                .setDescriptionFull(movie.getString("description_full")).setLanguage(movie.getString("language"))
                .setBgImageURL(movie.getString("background_image")).setBgImageOriginalURL(movie.getString("background_image_original"))
                .setSmallCoverImageUrl(movie.getString("small_cover_image")).setMediumCoverImageUrl(movie.getString("medium_cover_image"))
                .setLargeCoverImageUrl(movie.getString("large_cover_image")).setTorrents(torrents);

        loadMovieScreenshots(moviey);
        loadCast(moviey);
        loadSuggestedMovies(moviey);

        if (isSearch) {
            searcheMovies.add(moviey);
        } else {
            movies.add(moviey);
            notifyObserver();
            /**
             * Update the adapter every after a new movie insertion.
             */
            RecyclerAdapter adapter = MovieFragment.getInstance().getAdapter();
            if (adapter != null) {
                Log.i(TAG, "Notifing dataset change");
                adapter.notifyDataSetChanged();
            } else
                Log.i(TAG, "adapter == null");
            //adapter.notifyDataSetChanged();
        }
        return moviey;
    }

    public void search(String query) {
        searcheMovies.clear();
        AsyncTask<String, String, Object> worker = new SearchWorker();
        worker.execute(String.format(MOVIE_SEARCH_URL, query), query);


    }

    private  void loadCast(final Movie movie) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, String.format(MOVIE_DETAILS_URL, movie.getId()), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object   = response.getJSONObject("data");
                            JSONObject movieObj = object.getJSONObject("movie");
                            JSONArray castArray = movieObj.getJSONArray("cast");
                            for (int i = 0; i < castArray.length(); i++) {
                                JSONObject jsonObject = castArray.getJSONObject(i);
                                String urlImage = "";
                                try {
                                    urlImage = jsonObject.getString("url_small_image");
                                } catch (JSONException e) {
                                    urlImage = "https://yts.ag/assets/images/actors/thumb/default_avatar.jpg";
                                }
                                movie.addCast(
                                        new Cast(jsonObject.getString("name"),
                                                jsonObject.getString("character_name"),
                                                urlImage,
                                                jsonObject.getString("imdb_code")));
                                Log.i("Cast:", jsonObject.toString());
                            }
                        } catch (JSONException e) {
                            Log.i("Main:", e.toString());
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // TODO Auto-generated method stub
                        e.printStackTrace();
                        Log.i("Main: Error", e.toString());

                    }
                });
        requestQueue.add(jsObjRequest);
    }

    private  void loadMovieScreenshots(final Movie movie) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, String.format(MOVIE_DETAILS_URL, movie.getId()), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("data");
                            JSONObject movieObj = object.getJSONObject("movie");


                            movie.setScreenshot1(movieObj.getString("medium_screenshot_image1"));
                            movie.setScreenshot2(movieObj.getString("medium_screenshot_image2"));
                            movie.setScreenshot3(movieObj.getString("medium_screenshot_image3"));
                            //Log.i("Shot:", movie.getScreenshot1URL());
                        } catch (JSONException e) {
                            //Log.i("Main:", e.toString());
                            e.printStackTrace();
                        }
                        //Z@mtel100$

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // TODO Auto-generated method stub
                        e.printStackTrace();
                        Log.i("Main: Error", e.toString());

                    }
                });
        requestQueue.add(jsObjRequest);
    }

    public  void loadSuggestedMovies(final Movie movie) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, String.format(MOVIE_SUGGESTIONS, movie.getId()), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Movie> suggestedMovies = new ArrayList<>();
                        try {
                            JSONObject object = response.getJSONObject("data");
                            JSONArray m = object.getJSONArray("movies");
                            for (int i = 0; i < m.length(); i++) {
                                JSONObject _movie = m.getJSONObject(i);
                                suggestedMovies.add(createMovie(_movie));
                                //Log.i("SuggestedMovies", _movie.toString(4));
                            }
                            movie.setSuggestedMovies(suggestedMovies);
                        } catch (JSONException e) {
                            Log.i("Main:", e.toString());
                            e.printStackTrace();
                        }
                        //Z@mtel100$

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // TODO Auto-generated method stub
                        e.printStackTrace();
                        Log.i("Main: Error", e.toString());

                    }
                });
        requestQueue.add(jsObjRequest);
    }

    public void loadNextPage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestQueue = VolleySingleton.getInstance(ctx).getRequestQueue();
                Log.i(TAG, "Loading next page. Current page = " + currentPage);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, String.format(url, currentPage), null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject object = response.getJSONObject("data");
                                    JSONArray m = object.getJSONArray("movies");
                                    for (int i = 0; i < m.length(); i++) {
                                        final JSONObject movie = m.getJSONObject(i);
                                        addMovie(movie, false);
                                    }
                                } catch (Exception e) {
                                    Log.i("Main:", e.toString());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError e) {
                                Log.i("NetworkError", e.toString());
                            }
                        });
                synchronized (this){
                    currentPage += 1;
                }

                requestQueue.add(jsObjRequest);
            }
        }).start();


    }

    public static MovieLoader getInstance() {
        return __instance;
    }

    public static MovieLoader getInstance(Context ctx) {
        if (__instance == null) {
            __instance = new MovieLoader(ctx);
        }
        return __instance;
    }

    public ArrayList<Movie> getMovies() {
        Log.i("MovieCount", "" + movies.size());
        return movies;
    }

    public ArrayList<Movie> getSearchedMovies() {

        return searcheMovies;
    }


    public boolean isSearching() {
        return isLoading;
    }

    @Override
    public void register(MovieObserver observer) {
        Log.i(TAG, "Registering observer");
        observers.add(observer);
    }

    @Override
    public void remove(MovieObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver() {
        Log.i(TAG, "Calling observer. Movie Size = " + movies.size());
        for (MovieObserver o : observers)
            o.update();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    class Worker extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // isLoading = true;

        }


        @Override
        protected String doInBackground(String... args) {
            Log.i(TAG, "doInBackground");
            final boolean[] error = {false};
            requestQueue = VolleySingleton.getInstance(ctx).getRequestQueue();
            if (args.length > 1 && args[1].equals("invalidate")) {
                Log.i(TAG, "Invalidating cache");
                //requestQueue.getCache().clear();
            }
            Log.i(TAG, "Loading next page. Current page = " + currentPage);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, String.format(args[0], currentPage), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject object = response.getJSONObject("data");
                                JSONArray m = object.getJSONArray("movies");
                                for (int i = 0; i < m.length(); i++) {
                                    final JSONObject movie = m.getJSONObject(i);
                                    addMovie(movie, false);


                                }
                            } catch (Exception e) {
                                Log.i("Main:", e.toString());

                                error[0] = true;
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError e) {

                            error[0] = true;
                            Log.i("NetworkError", e.toString());
                        }
                    }) {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    Response<JSONObject> resp = super.parseNetworkResponse(response);
                    if (!resp.isSuccess()) {
                        return resp;
                    }
                    long now = System.currentTimeMillis();
                    Cache.Entry entry = resp.cacheEntry;
                    if (entry == null) {
                        entry = new Cache.Entry();
                        entry.data = response.data;
                        entry.responseHeaders = response.headers;
                    }
                    long cacheExpired = now + 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    entry.ttl = cacheExpired;
                    return Response.success(resp.result, entry);
                }
            };
            currentPage += 1;
            requestQueue.add(jsObjRequest);

            return null;
        }

        @Override
        protected void onPostExecute(String o) {

            MovieFragment.getInstance().setMovies(movies);
            //isLoading = false;
        }
    }


    class SearchWorker extends AsyncTask<String, String, Object> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoading = true;
            Log.i(TAG, "SearchWorker- started: isLoading = " + isSearching());
        }

        @Override
        protected void onCancelled() {
            isLoading = false;
            if (searcheMovies.size() > 0) {
                ArrayList<Movie> _searchedMovies = (ArrayList<Movie>) searcheMovies.clone();
                searcheMovies.clear();
                for (int i = _searchedMovies.size() - 1; i >= 0; i--) {
                    Log.i("Search", "i = " + i + " Size: = " + _searchedMovies.size());
                    searcheMovies.add(_searchedMovies.get(i));
                }
            }
            Log.i(TAG, "SearchWorker- Canceled: isLoading = " + isSearching());
        }

        ///
        @Override
        protected Object doInBackground(String... args) {
            requestQueue = VolleySingleton.getInstance(ctx).getRequestQueue();
            String url = args[0];
            final String query = args[1];
            final SearchQueryCache cache = SearchQueryCache.getInstance();
            if (cache.contains(query)) {
                Log.i(TAG, "Search Cache Hit for query: " + query);
                searcheMovies = cache.get(query);
                isLoading = false;
                cancel(false);
            } else {
                Log.i(TAG, "Search Cache Miss for query: " + query);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject object = response.getJSONObject("data");
                                    JSONArray m = object.getJSONArray("movies");
                                    ArrayList<Movie> searchResults = new ArrayList<>();
                                    for (int i = 0; i < m.length(); i++) {
                                        JSONObject movie = m.getJSONObject(i);
                                        searchResults.add(addMovie(movie, true));
                                        Log.i("Movies", movie.toString());
                                    }
                                    cache.put(query, searchResults);
                                } catch (JSONException e) {
                                    Log.i("Main:", e.toString());
                                    e.printStackTrace();
                                }

                                //Z@mtel100$
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError e) {
                                e.printStackTrace();
                                Log.i("Main: Error", e.toString());

                            }
                        });
                //currentPage += 1;
                requestQueue.add(jsObjRequest);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            isLoading = false;
            /**
             * Sort the search movie list from descending to ascending order ie,
             * recently searched movies put on top
             */
            if (searcheMovies.size() > 0) {
                ArrayList<Movie> _searchedMovies = (ArrayList<Movie>) searcheMovies.clone();
                searcheMovies.clear();
                for (int i = _searchedMovies.size() - 1; i >= 0; i--) {
                    // pop last element and add to new list
                    Log.i("Search", "i = " + i + " Size: = " + _searchedMovies.size());
                    searcheMovies.add(_searchedMovies.get(i));
                }
            }
            Log.i(TAG, "SearchWorker- finished: isLoading = " + isSearching());

        }
    }


}
