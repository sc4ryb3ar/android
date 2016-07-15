package com.bitlove.fetlife.notification;

import android.content.Intent;
import android.net.Uri;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.model.pojos.NotificationHistoryItem;

import org.json.JSONObject;

public class InfoNotification extends OneSignalNotification {

    public InfoNotification(String title, String message, String launchUrl, JSONObject additionalData, String id) {
        super(title, message,launchUrl,additionalData,id);
    }

    @Override
    public boolean process(FetLifeApplication fetLifeApplication) {

        createNotificationItem().save();

        //TODO: save notification
        return false;
    }

    protected NotificationHistoryItem createNotificationItem() {
        NotificationHistoryItem notificationItem = new NotificationHistoryItem();
        notificationItem.setDisplayHeader(title);
        notificationItem.setDisplayMessage(message);
        notificationItem.setLaunchUrl(launchUrl);
        return notificationItem;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        if (launchUrl != null && launchUrl.trim().length() != 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(launchUrl));
            fetLifeApplication.startActivity(intent);
        } else {
            //TODO: open notification history
        }
    }
}
