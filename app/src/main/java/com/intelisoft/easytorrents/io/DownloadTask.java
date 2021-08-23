package com.intelisoft.easytorrents.io;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.intelisoft.easytorrents.iface.TorrentDownloadListener;
import com.intelisoft.easytorrents.model.Movie;
import com.intelisoft.easytorrents.util.DownloadStatus;
import com.intelisoft.easytorrents.util.Utils;
import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Technophile on 6/5/17.
 */

public class DownloadTask implements Serializable{
    private Movie.Torrent torrent;
    private Context ctx;
    private TorrentDownloadListener downloadListener;

    public DownloadTask(Context ctx, Movie.Torrent torrent) {
        this.ctx = ctx;
        this.torrent = torrent;

    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Worker().execute("");
            }
        }).start();
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private Client client;


    public Movie.Torrent getTorrent() {
        return torrent;
    }



    public void addDownloadListener(TorrentDownloadListener listener) {
        downloadListener = listener;
    }

    private class Worker extends AsyncTask<String, Integer, Void> {
        boolean errors_occured = false;
        private  Config config;
        private File temp;
        String fileName;

        @Override
        protected void onPreExecute() {
            Log.i("DownloadTask", "onPreExecute");
            config = Config.getInstance();
            temp = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String[] data = torrent.getUrl().split("/");
            fileName = data[data.length-1];
            temp = new File(temp.getAbsolutePath() + "/" + fileName);
            try {
                config.addTorrent(fileName, temp.getAbsolutePath(), torrent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected Void doInBackground(String... strings) { //
            try {
                config.addTorrent(fileName, temp.getAbsolutePath(), torrent);
                temp.createNewFile();
                FileUtils.copyURLToFile(new URL(torrent.getUrl()), temp);
                client = new Client(Utils.getIPAddress(),
                        SharedTorrent.fromFile(temp, FileSystemIO.getInstance().getAppFolder()));
                client.addObserver(new Observer() {
                    @Override
                    public void update(Observable observable, Object data) {
                        Client client = (Client) observable;
                        float progress = client.getTorrent().getCompletion();
                        Log.i("DownloadTask", client.getTorrent().getName() + " " +progress+" %, Down: " + client.getTorrent().getDownloaded());
                        downloadListener.onProgress();
                    }
                });
                torrent.setTitle(client.getTorrent().getName().replace("[YTS.AG]",""));
                client.download();
                //client.waitForCompletion();
            } catch (Exception e) {
                Log.i("DownloadTask", e.getMessage());
                errors_occured = true;
                //e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (errors_occured) {
                Toast.makeText(ctx, "Could not download movie. Check your intenet connection and  try again.", Toast.LENGTH_LONG).show();
            }
            Log.i("DownloadTask", "onPost");
        }
    }
}
