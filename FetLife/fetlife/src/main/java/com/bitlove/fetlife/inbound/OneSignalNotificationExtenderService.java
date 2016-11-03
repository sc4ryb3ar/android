package com.bitlove.fetlife.inbound;

import android.content.SharedPreferences;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.NotificationReceivedEvent;
import com.bitlove.fetlife.notification.AnonymNotification;
import com.bitlove.fetlife.notification.NotificationParser;
import com.bitlove.fetlife.notification.OneSignalNotification;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

public class OneSignalNotificationExtenderService extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        FetLifeApplication fetLifeApplication = getFetLifeApplication();

        NotificationParser notificationParser = fetLifeApplication.getNotificationParser();
        OneSignalNotification oneSignalNotification = notificationParser.parseNotification(fetLifeApplication, notification);

        boolean handledInternally = oneSignalNotification.handle(fetLifeApplication);

        if (!handledInternally && oneSignalNotification.isEnabled(fetLifeApplication)) {
            if (useAnonymNotifications()) {
                AnonymNotification anonymNotification = new AnonymNotification();
                anonymNotification.display(getFetLifeApplication());
            } else {
                OverrideSettings overrideSettings = new OverrideSettings();
                OSNotificationDisplayedResult displayedResult  = displayNotification(overrideSettings);
                oneSignalNotification.onNotificationDisplayed(fetLifeApplication, displayedResult.androidNotificationId);
            }
        }

        fetLifeApplication.getEventBus().post(new NotificationReceivedEvent());

        return true;
    }

    private boolean useAnonymNotifications() {
        SharedPreferences sharedPreferences = getFetLifeApplication().getUserSessionManager().getActiveUserPreferences();
        return sharedPreferences.getBoolean(getFetLifeApplication().getString(R.string.settings_key_notification_anonymtext),false);
    }

    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getApplicationContext();
    }

}
