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

/**
 * Extension point for One Signal notification library to override default display and onclick behaviour
 */
public class OneSignalNotificationExtenderService extends NotificationExtenderService {

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        FetLifeApplication fetLifeApplication = getFetLifeApplication();

        //Parse the incoming notification so we can handle it accordingly based on its type
        NotificationParser notificationParser = fetLifeApplication.getNotificationParser();
        OneSignalNotification oneSignalNotification = notificationParser.parseNotification(fetLifeApplication, notification);

        //Handle the incoming notification to do what is needed at the state of onreceived.
        boolean handledInternally = oneSignalNotification.handle(fetLifeApplication);

        //Check if the Notification was not fully handled internally and if it is not disabled by the user settings
        if (!handledInternally && oneSignalNotification.isEnabled(fetLifeApplication)) {
            //Check if the user use settings for hiding details of the notifications
            if (useAnonymNotifications()) {
                AnonymNotification anonymNotification = new AnonymNotification();
                anonymNotification.display(getFetLifeApplication());
            } else {
                OverrideSettings overrideSettings = new OverrideSettings();
                OSNotificationDisplayedResult displayedResult  = displayNotification(overrideSettings);
                //Let the notification react on notification displayed state so it can get the displayed notification identifier
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
