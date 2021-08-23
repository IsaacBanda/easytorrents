package com.intelisoft.easytorrents.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.io.VolleySingleton;
import com.intelisoft.easytorrents.model.Cast;
import com.intelisoft.easytorrents.model.Movie;
import com.intelisoft.easytorrents.ui.CircleImageView;

import java.util.ArrayList;

/**
 * Created by Technophile on 11/27/16.
 */

public class CastAdapter extends BaseAdapter {
    private ArrayList<Cast> casts;
    private Context ctx;

    public CastAdapter(Context ctx, Movie movie) {
        casts = movie.getCast();
        this.ctx = ctx;
    }


    @Override
    public int getCount() {
        return casts.size();
    }

    @Override
    public Cast getItem(int i) {
        return casts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Cast cast = getItem(i);
        View row;
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.cast_row_item, viewGroup, false);
        } else {
            row = convertView;
        }

        CircleImageView imageView = (CircleImageView) row.findViewById(R.id.cast_image);
        TextView tvName = (TextView) row.findViewById(R.id.tvCastName);
        TextView tvFakeName = (TextView) row.findViewById(R.id.tvCastFakeName);
        ImageLoader mImageLoader = VolleySingleton.getInstance(row.getContext()).getImageLoader();
        mImageLoader.get(cast.getUrlSmallImage(), ImageLoader.getImageListener(imageView,
                R.mipmap.ic_launcher, android.R.drawable
                        .ic_dialog_alert));
        imageView.setImageUrl(cast.getUrlSmallImage(), mImageLoader);
        tvName.setText(cast.getName());
        tvFakeName.setText(" as " + cast.getCharacterName());
        return row;
    }
}
