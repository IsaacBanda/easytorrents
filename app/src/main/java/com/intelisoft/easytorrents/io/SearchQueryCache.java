package com.intelisoft.easytorrents.io;

import com.intelisoft.easytorrents.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Technophile on 6/2/17.
 */

public class SearchQueryCache {
    private static SearchQueryCache _instance;
    private Map<String, ArrayList<Movie>> searchCache;

    private SearchQueryCache() {
        searchCache = new HashMap<>();
    }

    public static SearchQueryCache getInstance() {
        if (_instance == null)
            _instance = new SearchQueryCache();
        return _instance;
    }


    public boolean contains(String query) {
        return searchCache.containsKey(query);
    }

    public ArrayList<Movie> get(String query) {
        return searchCache.get(query);
    }

    public void put(String query, ArrayList<Movie> searcheMovies) {
        searchCache.put(query, searcheMovies);
    }
}
