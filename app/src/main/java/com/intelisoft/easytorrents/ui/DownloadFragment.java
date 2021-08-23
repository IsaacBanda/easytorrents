package com.intelisoft.easytorrents.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.adapter.DownloadTaskAdapter;
import com.intelisoft.easytorrents.iface.TorrentDownloadListener;
import com.intelisoft.easytorrents.io.Config;
import com.intelisoft.easytorrents.io.DownloadTask;
import com.intelisoft.easytorrents.model.Movie;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;


public class DownloadFragment extends Fragment implements TorrentDownloadListener {

    private ListView downloadListView;
    private DownloadTaskAdapter downloadTaskAdapter;
    private static LinkedList<DownloadTask> downloadTaskList;
    private static DownloadFragment _instance;


    public DownloadFragment() {
        downloadTaskList = new LinkedList<>();

    }

    public void addDownloadTask(DownloadTask task) {
        Log.i("DownloadFragment", "addDownloadTask");
        if (downloadListView == null) {
            Log.i("DownloadFragment", "downloadListView == null");
            return;
        }
        // Check if already exists
        for (DownloadTask t : downloadTaskList) {
            if (t.getTorrent().getTitle().contains(task.getTorrent().getTitle())) {
                Toast.makeText(getContext(), "Movie is already downloading", Toast.LENGTH_LONG).show();
                return;
            }
        }
        task.addDownloadListener(this);
        downloadTaskList.add(task);
        task.start();
        DownloadTaskAdapter adapter = (DownloadTaskAdapter) downloadListView.getAdapter();
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        downloadListView = (ListView) view.findViewById(R.id.lv_torrent_downloads);
        downloadListView.setAdapter( new DownloadTaskAdapter(getActivity(), downloadTaskList));

        try {
            //Load incomplete downloads
            Config config = Config.getInstance();
            JSONArray downloadedMovies = config.getDownloadedMovies();
            Log.i("Main: ", "Array size: " + downloadedMovies.length());
            if (downloadedMovies.length() > 0) {
                for (int i = 0; i < downloadedMovies.length(); i++) {
                    JSONObject object = downloadedMovies.getJSONObject(i);

                    Movie.Torrent torrent = new Movie.Torrent(object.getString("title"), object.getString("url"), object.getString("size"));
                    DownloadTask task = new DownloadTask(this.getActivity(), torrent);
                    Log.i("Main", "Adding new task object");
                    DownloadFragment.getInstance().addDownloadTask(task);
                }
            }
        } catch (Exception e){}

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download, container, false);
    }


    public static DownloadFragment getInstance() {
        if (_instance == null)
            _instance = new DownloadFragment();
        return _instance;
    }

    @Override
    public void onProgress() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DownloadTaskAdapter adapter = (DownloadTaskAdapter) downloadListView.getAdapter();
                    adapter.notifyDataSetChanged();
                }
            });
        }

    }
}
