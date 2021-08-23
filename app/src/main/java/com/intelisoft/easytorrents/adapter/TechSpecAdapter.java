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

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Technophile on 6/2/17.
 */

public class TechSpecAdapter extends BaseAdapter {
    private ArrayList<Movie.Torrent> torrents = new ArrayList<>();
    private Context ctx;

    public TechSpecAdapter(Context ctx, Movie movie) {
        torrents = movie.getTorrents();
        this.ctx = ctx;
    }

    @Override
    public int getCount() {

        return torrents.size();
    }

    @Override
    public Movie.Torrent getItem(int i) {
        return torrents.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Movie.Torrent torrent = getItem(i);
        View row;
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.torrent_details_list_item, viewGroup, false);
        } else {
            row = convertView;
        }

        TextView tvQuality     = (TextView)  row.findViewById(R.id.tv_torrent_quality),
                tvDateUploaded = (TextView)  row.findViewById(R.id.tv_torrent_date_uploaded),
                size           = (TextView)  row.findViewById(R.id.tv_torrent_file_size),
                tvRuntime      = (TextView)  row.findViewById(R.id.tv_torrent_runtime);

        ///////////////////////////////////////////////
        tvQuality.setText(torrent.getQuality());
        tvDateUploaded.setText(torrent.getDateUploaded());
        int runtime = Integer.parseInt(torrent.getRuntime());
        tvRuntime.setText(String.format("%dh:%dm", (runtime/60), (runtime%60)));
        size.setText(torrent.getSize());


        return row;

    }
}
