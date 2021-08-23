package com.intelisoft.easytorrents.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.intelisoft.easytorrents.R;
import com.intelisoft.easytorrents.io.DownloadTask;
import com.intelisoft.easytorrents.io.FileSystemIO;
import com.intelisoft.easytorrents.model.Movie;
import com.intelisoft.easytorrents.util.Utils;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.workarounds.typography.Button;

public class AddTorrentActivity extends AppCompatActivity {

    private FileSystemIO fileSystemIO;

    @BindView(R.id.tv_torrent_name)  TextView torrentSizeTextView;
    @BindView(R.id.tv_torrent_save_path)  TextView torrentSavePathTextView;
    @BindView(R.id.btn_torrent_change_download_path)  Button torrentChangePathButton;
    @BindView(R.id.btn_torrent_start_download)  Button torrentDownloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_torrent);
        ButterKnife.bind(this);

        FileSystemIO.setContext(this);
        fileSystemIO = FileSystemIO.getInstance();

        Intent intent = getIntent();
        final Movie.Torrent torrent = (Movie.Torrent) intent.getSerializableExtra("torrent");
        int callToActionBtnRgbColor = intent.getIntExtra("swatchRgb", 0);



        torrentSizeTextView.setText(torrent.getTitle());
        torrentSavePathTextView.setText(fileSystemIO.getExternalStorageDir().getAbsolutePath());
        int btnRgb = (callToActionBtnRgbColor != 0) ? callToActionBtnRgbColor :  getResources().getColor(R.color.colorPrimary);
        torrentDownloadButton.setBackgroundColor((btnRgb));

        ///////////////
        torrentChangePathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChooserDialog().with(AddTorrentActivity.this)
                        .withFilter(true, false)
                        .withStartFile(FileSystemIO.getInstance().getAppFolder().getAbsolutePath())
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                torrentSavePathTextView.setText(path);
                                //Toast.makeText(AddTorrentActivity.this, "FOLDER: " + path, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build()
                        .show();
            }
        });
        torrentDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FileSystemIO.getInstance().canWriteToStorage()) {
                    Toast.makeText(AddTorrentActivity.this, "It seems i can't write to storage. Kindly give me permissions. ", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Utils.isConnectivityAvailable(AddTorrentActivity.this)) {
                    Toast.makeText(AddTorrentActivity.this,
                            "No active network connection detected. Aborting download.", Toast.LENGTH_SHORT).show();
                    return;
                }
                DownloadFragment.getInstance()
                        .addDownloadTask(new DownloadTask(AddTorrentActivity.this, torrent));
                Toast.makeText(AddTorrentActivity.this, "Starting download....", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
    }

    public void closeMe(View view) {
        finish();
        super.finish();
    }



}
