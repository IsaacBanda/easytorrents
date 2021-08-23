package com.intelisoft.easytorrents.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Movie;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.util.List;

/**
 * Created by Technophile on 5/28/17.
 */

public class MovieSearchAdapter extends CursorAdapter {

    private List<Movie> movies;

    public MovieSearchAdapter(Context context, Cursor cursor, List<Movie> movies) {
        super(context, cursor, false);
        this.movies  = movies;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
