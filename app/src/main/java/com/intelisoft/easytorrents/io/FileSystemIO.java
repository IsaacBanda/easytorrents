package com.intelisoft.easytorrents.io;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Technophile on 6/4/17.
 */

public class FileSystemIO {
    private static FileSystemIO _instance;
    private Context ctx;
    private final String APP_ROOT_NAME = "YTSTorrents";
    private final String TAG = "FileSystemIO";
    private File folderDir;
    final String PREF_FOLDER_DOWNLOADS_DEFAULT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + APP_ROOT_NAME;

    private FileSystemIO(Context ctx) {
        this.ctx = ctx;
        checkConfigs();
    }

    private void checkConfigs() {
        this.folderDir = new File(PREF_FOLDER_DOWNLOADS_DEFAULT);
        if (!this.folderDir.exists()) {
            this.folderDir.mkdirs();
        }
    }

    public static void setContext(Context c){
        _instance = new FileSystemIO(c);
    }

    public static FileSystemIO getInstance() {
        if (_instance == null)
            throw new IllegalArgumentException("Context can to be null. Did you call setContext first?");
        return _instance;
    }


    public File getExternalStorageDir() {
        return Environment.getExternalStorageDirectory();
    }

    public File  getAppFolder() {
        return this.folderDir;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public  boolean canWriteToStorage()
    {

        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = ctx.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
