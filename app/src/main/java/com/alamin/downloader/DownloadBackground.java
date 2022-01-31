package com.alamin.downloader;

import static android.content.Context.DOWNLOAD_SERVICE;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class DownloadBackground extends AsyncTask<String, Integer, String> {
    private String TAG = "DownloadBackground";

    private String videoUrl = "http://smweb.suffixit.com:8357/videos/sunmoon_av_video.mp4";
    private String name = "TMA Video";
    private String downloadFileName = "sample_video.mp4";
    DownloadManager downloadManager;
    private Context context;
    private long downloadID;
    private TextView textView;

    public DownloadBackground(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    @Override
    protected String doInBackground(String... strings) {
        beginDownload();
        return "Download Complete";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        textView.setText(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        textView.setText("Downloading : " + values[0]);
    }

    @SuppressLint("NewApi")
    public void beginDownload() {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoUrl))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)// Visibility of the download Notification
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadFileName)
                .setTitle(name)
                .setDescription("Downloading")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.

        boolean finishDownload = false;
        int progress;
        while (!finishDownload) {
            Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(downloadID));
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        Log.d(TAG, "Download " + "ERROR_CANNOT_RESUME");
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        Log.d(TAG, "Download " + "ERROR_DEVICE_NOT_FOUND");
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        Log.d(TAG, "Download " + "ERROR_FILE_ALREADY_EXISTS");
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        Log.d(TAG, "Download " + "ERROR_FILE_ERROR");
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        Log.d(TAG, "Download " + "ERROR_HTTP_DATA_ERROR");
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        Log.d(TAG, "Download " + "ERROR_INSUFFICIENT_SPACE");
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        Log.d(TAG, "Download " + "ERROR_TOO_MANY_REDIRECTS");
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        Log.d(TAG, "Download " + "ERROR_UNHANDLED_HTTP_CODE");
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        Log.d(TAG, "Download " + "ERROR_UNKNOWN");
                        break;
                    case DownloadManager.STATUS_FAILED: {
                        finishDownload = true;
                        Log.d(TAG, "Download " + "STATUS_FAILED");
                        break;
                    }
                    case DownloadManager.STATUS_PAUSED:
                        Log.d(TAG, "Download " + "STATUS_PAUSED");
                        break;

                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        Log.d(TAG, "Download " + "PAUSED_QUEUED_FOR_WIFI");
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        Log.d(TAG, "Download " + "PAUSED_WAITING_TO_RETRY");
                        break;

                    case DownloadManager.STATUS_RUNNING: {
                        @SuppressLint("Range") final long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        if (total >= 0) {
                            @SuppressLint("Range") final long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            progress = (int) ((downloaded * 100L) / total);
                            publishProgress(progress);
                        }
                        break;
                    }
                    case DownloadManager.STATUS_SUCCESSFUL: {
                        progress = 100;
                        publishProgress(progress);
                        finishDownload = true;
                        break;
                    }
                }
            }
        }
    }

}
