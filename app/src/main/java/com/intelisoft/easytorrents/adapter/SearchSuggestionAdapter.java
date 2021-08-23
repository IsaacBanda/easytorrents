package com.intelisoft.easytorrents.adapter;

import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.io.MovieLoader;
import com.intelisoft.easytorrents.io.VolleySingleton;
import com.intelisoft.easytorrents.model.Movie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;


/**
 * Created by Technophile on 5/28/17.
 */

public class SearchSuggestionAdapter extends SimpleCursorAdapter {
    private static final String[] mFields  = { "_id", "title" ,"imgUrl", "smallImgUrl"};
    private static final String[] mVisible = { "title", "imgUrl", "smallImgUrl" };
    private static final int[]    mViewIds = { android.R.id.text1 };
    private final int TITLE = 0x1, IMAGE_URL = 0x2, SMALL_IMAGE_URL = 0x3;
    private Context ctx;

    public SearchSuggestionAdapter(Context context)
    {
        super(context, R.layout.movie_search_list_item, null, mVisible, mViewIds, 0);
        ctx = context;
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint)
    {
        return new SuggestionsCursor(constraint);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView tvName = (TextView) view.findViewById(R.id.title);
        NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.thumbnail);

        tvName.setText(cursor.getString(TITLE));

    }

    private class SuggestionsCursor extends AbstractCursor
    {
        private ArrayList<Movie> mResults;

        public SuggestionsCursor(CharSequence constraint)
        {

            mResults = MovieLoader.getInstance(ctx).getSearchedMovies();


        }

        @Override
        public int getCount()
        {
            return mResults.size();
        }

        @Override
        public String[] getColumnNames()
        {
            return mFields;
        }

        @Override
        public long getLong(int column)
        {
            if(column == 0){
                return mPos;
            }
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public String getString(int column)
        {
            if(column == TITLE){
                return mResults.get(mPos).getTitle();
            }
            if (column == IMAGE_URL) {
                return mResults.get(mPos).getMediumCoverImageUrl();
            }

            if (column == SMALL_IMAGE_URL) {
                return mResults.get(mPos).getSmallCoverImageUrl();
            }
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public short getShort(int column)
        {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public int getInt(int column)
        {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public float getFloat(int column)
        {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public double getDouble(int column)
        {
            throw new UnsupportedOperationException("unimplemented");
        }

        @Override
        public boolean isNull(int column)
        {
            return false;
        }
    }
}
