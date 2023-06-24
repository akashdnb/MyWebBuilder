package com.example.mywebbuilder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.mywebbuilder.utils.DirectoryUtil;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    boolean isSplashDisplayed = false;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupActivity();
    }

    private void setupActivity(){
        new Handler().postDelayed(() -> {
            if (isSplashDisplayed) {
                startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                finish();
            }
            isSplashDisplayed = true;
        }, 3000);

        if (isReadStoragePermissionGranted()) {
            try {
                DirectoryUtil.createFleFolder();
                if (isSplashDisplayed) {
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                    finish();
                }
                isSplashDisplayed = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        }
    }

    private boolean isReadStoragePermissionGranted() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DirectoryUtil.createFleFolder();
                try {
                    if (isSplashDisplayed) {
                        startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                        finish();
                    }
                    isSplashDisplayed = true;
                } catch (Exception e) {
                    Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "storage permission needed!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
}