package com.bitlove.fetlife.notification;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.util.ApkUtil;

public class UpdateBroadcastReceiver extends BroadcastReceiver {

    public static final String EXTRA_URL = "EXTRA_URL";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            ApkUtil.installApk(FetLifeApplication.getInstance(),intent.getStringExtra(EXTRA_URL));
        } else {
            UpdatePermissionActivity.startActivity(context,intent.getStringExtra(EXTRA_URL));
        }
    }
}
