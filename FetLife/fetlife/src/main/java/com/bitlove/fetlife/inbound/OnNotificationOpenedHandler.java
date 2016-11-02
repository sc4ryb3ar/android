package com.bitlove.fetlife.inbound;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.notification.NotificationParser;
import com.bitlove.fetlife.notification.OneSignalNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

public class OnNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        FetLifeApplication fetLifeApplication = FetLifeApplication.getInstance();
        if (result == null) {
            return;
        }



        OneSignalNotification oneSignalNotification = fetLifeApplication.getNotificationParser().parseNotification(fetLifeApplication, result.notification.payload.additionalData.optString(NotificationParser.JSON_FIELD_STRING_TITLE), result.notification.payload.body, result.notification.payload.additionalData.optString(NotificationParser.JSON_FIELD_STRING_LAUNCHURL), result.notification.payload.additionalData, null, result.notification.payload.additionalData.optString(NotificationParser.JSON_FIELD_STRING_GROUP));
        oneSignalNotification.onClick(fetLifeApplication);
    }

}
