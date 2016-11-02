package com.bitlove.fetlife.inbound;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.notification.NotificationParser;
import com.bitlove.fetlife.notification.OneSignalNotification;
import com.bitlove.fetlife.util.RemoteLogger;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.Iterator;

public class OnNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {

        try {
            FetLifeApplication.getInstance().showLongToast(OnNotificationOpenedHandler.class.getSimpleName() + ": pushMessageArrived");
            RemoteLogger.appendLog(FetLifeApplication.getInstance(), OnNotificationOpenedHandler.class.getSimpleName() + ": pushMessageArrived");
            RemoteLogger.appendLog(FetLifeApplication.getInstance(), OnNotificationOpenedHandler.class.getSimpleName() + ":\t " + "message" + ":" + message);
            Iterator<String> iterator = additionalData.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                RemoteLogger.appendLog(FetLifeApplication.getInstance(), OnNotificationOpenedHandler.class.getSimpleName() + ":\t " + key + ":" + additionalData.get(key));
            }
        } catch (Throwable t) {
            RemoteLogger.appendLog(FetLifeApplication.getInstance(), OnNotificationOpenedHandler.class.getSimpleName() + ": logging failed due to: " + t.getMessage());
        }

        FetLifeApplication fetLifeApplication = FetLifeApplication.getInstance();
        if (additionalData == null) {
            return;
        }
        OneSignalNotification oneSignalNotification = fetLifeApplication.getNotificationParser().parseNotification(fetLifeApplication, additionalData.optString(NotificationParser.JSON_FIELD_STRING_TITLE), message, additionalData.optString(NotificationParser.JSON_FIELD_STRING_LAUNCHURL), additionalData, null, additionalData.optString(NotificationParser.JSON_FIELD_STRING_GROUP));
        oneSignalNotification.onClick(fetLifeApplication);
    }
}
