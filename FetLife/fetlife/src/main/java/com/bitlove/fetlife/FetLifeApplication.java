package com.bitlove.fetlife;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bitlove.fetlife.inbound.OnNotificationOpenedHandler;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.resource.ImageLoader;
import com.bitlove.fetlife.notification.NotificationParser;
import com.bitlove.fetlife.session.UserSessionManager;
import com.bitlove.fetlife.util.SecurityUtil;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.config.FlowManager;

import io.fabric.sdk.android.Fabric;
import org.greenrobot.eventbus.EventBus;

public class FetLifeApplication extends Application {

    private static final String APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED = "APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED";

    private static FetLifeApplication instance;

    private ImageLoader imageLoader;
    private NotificationParser notificationParser;
    private FetLifeService fetLifeService;

    private String versionText;
    private int versionNumber;
    private Activity foregroundActivity;

    private String accessToken;

    private EventBus eventBus;
    private UserSessionManager userSessionManager;

    public static FetLifeApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Setup default instance and callbacks
        instance = this;

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText = pInfo.versionName;
            versionNumber = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionText = getString(R.string.text_unknown);
        }

        registerActivityLifecycleCallbacks(new ForegroundActivityObserver());

        //Init crash logging
        Fabric.with(this, new Crashlytics());

        //Init push notifications
        OneSignal.startInit(this).setNotificationOpenedHandler(new OnNotificationOpenedHandler()).init();
        OneSignal.enableNotificationsWhenActive(true);

        //Init user session manager
        userSessionManager = new UserSessionManager(this);

        applyVersionUpgradeIfNeeded();

        userSessionManager.init();

        //Init members
        try {
            fetLifeService = new FetLifeService(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        imageLoader = new ImageLoader(this);
        notificationParser = new NotificationParser();
        eventBus = EventBus.getDefault();

    }

    public UserSessionManager getUserSessionManager() {
        return userSessionManager;
    }

    public void showToast(final int resourceId) {
        showToast(getResources().getString(resourceId));
    }

    public void showToast(final String text) {
        if (foregroundActivity != null && !foregroundActivity.isFinishing()) {
            foregroundActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(foregroundActivity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean isAppInForeground() {
        return foregroundActivity != null;
    }

    public Activity getForegroundActivity() {
        return foregroundActivity;
    }

    public FetLifeService getFetLifeService() {
        return fetLifeService;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public NotificationParser getNotificationParser() {
        return notificationParser;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public String getVersionText() {
        return versionText;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    private void applyVersionUpgradeIfNeeded() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersionUpgrade = sharedPreferences.getInt(APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED, 0);
        if (lastVersionUpgrade >= versionNumber) {
            return;
        }

        FlowManager.destroy();
        deleteDatabase(FetLifeDatabase.NAME + ".db");
        openOrCreateDatabase(FetLifeDatabase.NAME + ".db", Context.MODE_PRIVATE, null);

        sharedPreferences.edit().putInt(APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED, versionNumber).apply();
    }

    private class ForegroundActivityObserver implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            foregroundActivity = activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            foregroundActivity = null;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (foregroundActivity == null && !activity.isChangingConfigurations()) {
                userSessionManager.onAppInBackground();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

}

