package com.alamin.downloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    public static final int STORAGE_REQUEST_CODE = 2000;
    private static long UPDATE_INTERVAL = 5000;
    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Button btnDownload;

    private String videoUrl = "http://smweb.suffixit.com:8357/videos/sunmoon_av_video.mp4";
    private String name = "TMA Video";
    private String downloadFileName = "sample_video.mp4";
    DownloadManager downloadManager;
    long downloadID;

    // using broadcast method
    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }else {
                Log.d(TAG, "Download "+"Failed");

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDownload = findViewById(R.id.btnDownload);
        // using broadcast method
        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginDownload();
            }
        });
    }
    private void beginDownload(){

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)// Visibility of the download Notification
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,  downloadFileName)
                .setTitle(name)// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);// Set if download is allowed on roaming network
         downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.



        // using query method
        boolean finishDownload = false;
        int progress;
        while (!finishDownload) {
            Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadID));
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    case DownloadManager.STATUS_FAILED: {
                        finishDownload = true;
                        Log.d(TAG, "Download "+"Failed");
                        break;
                    }
                    case DownloadManager.STATUS_PAUSED:
                        Log.d(TAG, "Download "+"Paused");
                        break;

                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        Log.d(TAG, "Download "+"Pending");
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        Log.d(TAG, "Download "+"Pending");
                        break;

                    case DownloadManager.STATUS_RUNNING: {
                        @SuppressLint("Range") final long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        Log.d(TAG, "Download "+"Progress");

                        if (total >= 0) {
                            @SuppressLint("Range") final long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            progress = (int) ((downloaded * 100L) / total);
                            // if you use downloadmanger in async task, here you can use like this to display progress.
                            // Don't forget to do the division in long to get more digits rather than double.
                            //  publishProgress((int) ((downloaded * 100L) / total));
                          //  Toast.makeText(MainActivity.this, "Download Progress "+progress, Toast.LENGTH_SHORT).show();

                        }
                        break;
                    }
                    case DownloadManager.STATUS_SUCCESSFUL: {
                        Log.d(TAG, "Download "+"Complete");

                        progress = 100;
                        // if you use aysnc task
                        // publishProgress(100);
                        finishDownload = true;
                        Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
            Log.d(TAG, "beginDownload ");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // using broadcast method
        unregisterReceiver(onDownloadComplete);
    }
}

