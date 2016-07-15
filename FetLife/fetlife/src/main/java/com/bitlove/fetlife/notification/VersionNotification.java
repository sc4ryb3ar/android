package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.model.pojos.NotificationHistoryItem;

import org.json.JSONObject;

public class VersionNotification extends InfoNotification {

    public static final String NOTIFICATION_GROUP = "VersionNotification";

    public VersionNotification(String title, String message, String launchUrl, JSONObject additionalData, String id) {
        super(title, message,launchUrl,additionalData,id);
    }

    @Override
    public boolean process(FetLifeApplication fetLifeApplication) {
        return super.process(fetLifeApplication);
    }

    @Override
    protected NotificationHistoryItem createNotificationItem() {
        NotificationHistoryItem notificationItem = super.createNotificationItem();
        notificationItem.setGroup(NOTIFICATION_GROUP);
        return notificationItem;
    }
}
