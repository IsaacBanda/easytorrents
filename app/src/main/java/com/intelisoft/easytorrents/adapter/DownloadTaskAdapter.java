package com.intelisoft.easytorrents.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.io.DownloadTask;
import com.intelisoft.easytorrents.util.Utils;

import java.util.LinkedList;

/**
 * Created by Technophile on 6/5/17.
 */

public class DownloadTaskAdapter extends BaseAdapter {
    private LinkedList<DownloadTask> downloadTaskList;
    private Context ctx;

    public DownloadTaskAdapter(Context ctx, LinkedList<DownloadTask> downloadTaskList ) {
        this.downloadTaskList = downloadTaskList;
        this.ctx = ctx;
    }

    public boolean add(DownloadTask downloadTask) {
        if (downloadTaskList.contains(downloadTask))
            return false;
        downloadTaskList.add(downloadTask);
        return true;
    }

    @Override
    public int getCount() {
        return downloadTaskList.size();
    }

    @Override
    public DownloadTask getItem(int i) {
        return downloadTaskList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View row;
        DownloadTask task = getItem(i);
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.torrent_download_list_item, viewGroup, false);
        } else {
            row = convertView;
        }

        CircleProgressBar progressBar = (CircleProgressBar) row.findViewById(R.id.progressBar);
        TextView tvMovieName = (TextView) row.findViewById(R.id.download_torent_title);
        TextView tvFDownloaded = (TextView) row.findViewById(R.id.torrent_downloaded_size);
        TextView tvTorrentSize = (TextView) row.findViewById(R.id.torrent_size);

        tvMovieName.setText(task.getTorrent().getTitle());
        if (task.getClient() != null) {
            // Torrent has started downloaing
            float downloaded = Float.parseFloat(String.format("%.2f",((float)task.getClient().getTorrent().getDownloaded() / Utils.MB)));
            float size = Float.parseFloat(String.format("%.2f",((float)task.getClient().getTorrent().getSize() / Utils.MB)));
            progressBar.setProgress((int) task.getClient().getTorrent().getCompletion());
            tvFDownloaded.setText(downloaded +" MB / ");
            tvTorrentSize.setText(size + " MB ");
            Log.i("Adapter", "Torrent State: " +task.getClient().getState().toString());
        } else {
            //obviously, it hasn't started downloading yet.
            progressBar.setProgress(0);
            tvFDownloaded.setText("0 MB / ");
        }

        return row;
    }
}
