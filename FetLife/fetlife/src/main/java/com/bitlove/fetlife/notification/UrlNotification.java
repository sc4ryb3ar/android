package com.bitlove.fetlife.notification;

import android.content.Intent;
import android.net.Uri;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

public class UrlNotification extends OneSignalNotification {

    public UrlNotification(String message, String launchUrl, JSONObject additionalData, String id) {
        super(message,launchUrl,additionalData,id);
    }

    @Override
    public void process(FetLifeApplication fetLifeApplication) {
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(launchUrl));
        fetLifeApplication.startActivity(intent);
    }

}
