package com.bitlove.fetlife.inbound;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.notification.OneSignalNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class OnNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
        FetLifeApplication fetLifeApplication = FetLifeApplication.getInstance();

        OneSignalNotification oneSignalNotification = fetLifeApplication.getNotificationParser().parseNotification(message, null, additionalData, null);
        oneSignalNotification.onClick(fetLifeApplication);
    }
}
