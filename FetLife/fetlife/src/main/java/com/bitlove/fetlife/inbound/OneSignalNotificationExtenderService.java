package com.bitlove.fetlife.inbound;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.notification.NotificationParser;
import com.bitlove.fetlife.notification.OneSignalNotification;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationPayload;

public class OneSignalNotificationExtenderService extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationPayload notification) {
        FetLifeApplication fetLifeApplication = getFetLifeApplication();

        NotificationParser notificationParser = fetLifeApplication.getNotificationParser();
        OneSignalNotification oneSignalNotification = notificationParser.parseNotification(notification.message, notification.launchUrl, notification.additionalData, notification.notificationId);

        return oneSignalNotification.process(fetLifeApplication);
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplicationContext();
    }

}
