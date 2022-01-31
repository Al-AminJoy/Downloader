package com.alamin.downloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    public static final int STORAGE_REQUEST_CODE = 2000;
    private static long UPDATE_INTERVAL = 5000;
    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Button btnDownload;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDownload = findViewById(R.id.btnDownload);
        txt = findViewById(R.id.txt);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReadWritePermissionGranted(MainActivity.this)) {
                    downloadVideo();
                } else {
                    verifyPermissions(MainActivity.this);
                }
            }

        });
    }

    private void downloadVideo() {
        DownloadBackground downloadBackground = new DownloadBackground(getApplicationContext(), txt);
        downloadBackground.execute("");
    }

    private boolean isReadWritePermissionGranted(Activity activity) {
        int writePermission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED ||
                readPermission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private boolean verifyPermissions(Activity activity) {
        try {
            if (!isReadWritePermissionGranted(activity)) {
                try {
                    ActivityCompat.requestPermissions(
                            activity,
                            PERMISSIONS_REQ,
                            STORAGE_REQUEST_CODE
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case STORAGE_REQUEST_CODE:
                boolean update = false;
                for (int i = 0; i < grantResults.length; i++) {
                    update = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
                if (update) {
                    downloadVideo();
                } else {
                    verifyPermissions(MainActivity.this);
                }
                break;
        }
    }

}

