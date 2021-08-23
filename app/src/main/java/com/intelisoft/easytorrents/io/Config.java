package com.intelisoft.easytorrents.io;


import android.util.Log;

import com.intelisoft.easytorrents.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;



public class Config {
    private static Config __instance;
    private JSONArray array;
    File f;
    private final String TAG = "Config";

    private Config() {
        f = new File(FileSystemIO.getInstance().getAppFolder() + File.separator + "torrents");
        if (!f.exists()) {
            array = new JSONArray();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();

            }
        } else  {
            try {
                Scanner scanner = new Scanner(f);
                if (scanner.hasNext()) {
                    StringBuilder builder = new StringBuilder();
                    while (scanner.hasNext()) {
                        builder.append(scanner.nextLine());
                    }
                    String data = builder.toString();//scanner.nextLine();
                    Log.i(TAG, "Loaded: " + data);
                    array = new JSONArray(data);
                } else {
                    array = new JSONArray();
                }
                scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
                array = new JSONArray();
            }
        }
    }

    public static Config getInstance() {
        if (__instance == null)
            __instance = new Config();
        return __instance;
    }

    public synchronized  void addTorrent(String filename, String path, Movie.Torrent torrent) throws JSONException {
        for (int i=0; i<array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            if (object.getString("title").equals(torrent.getTitle()) && object.getString("url").equals(torrent.getUrl()))  {
                Log.i(TAG, "Movie already exists. Not saving it");
                return;
            }
        }
        array.put( new JSONObject()
                .put(filename, path)
                .put("size", torrent.getSize())
                .put("title", torrent.getTitle())
                .put("url", torrent.getUrl()));
       // Log.i(TAG, "put: " + array.toString());
    }

    public void save() throws IOException {
        if (array != null && array.length() > 0) {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(array.toString().getBytes());
            fos.close();
            Log.i(TAG, "Config saved");
        }
    }


    public JSONArray getDownloadedMovies() {
        return array;

    }
}
