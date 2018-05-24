package com.bitlove.fetlife.notification;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.util.ApkUtil;
import com.bitlove.fetlife.view.dialog.PictureUploadSelectionDialog;
import com.bitlove.fetlife.view.dialog.VideoUploadSelectionDialog;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.EventsActivity;

public class UpdatePermissionActivity extends Activity {

    private static final String EXTRA_URL = "EXTRA_URL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStoragePermission();
    }

    public static void startActivity(Context context, String url ) {
        Intent intent = new Intent(context,UpdatePermissionActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ApkUtil.installApk(FetLifeApplication.getInstance(),url);finish();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
        finish();
    }
}
