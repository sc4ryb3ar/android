package com.bitlove.fetlife;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.bitlove.fetlife.inbound.OnNotificationOpenedHandler;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.model.db.UserDatabaseHolder;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Users;
import com.bitlove.fetlife.model.resource.ImageLoader;
import com.bitlove.fetlife.notification.NotificationParser;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import io.fabric.sdk.android.Fabric;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FetLifeApplication extends Application {

    public static final String CONSTANT_PREF_KEY_ME_JSON = "com.bitlove.fetlife.bundle.json";
    private static final String CONSTANT_PREF_KEY_USERS_JSON = "com.bitlove.fetlife.users.json";
    private static final String CONSTANT_PREF_KEY_DB_VERSION = "com.bitlove.fetlife.pref.db_version";

    public static final String CONSTANT_ONESIGNAL_TAG_VERSION = "version";
    public static final String CONSTANT_ONESIGNAL_TAG_NICKNAME = "nickname";
    public static final String CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN = "member_token";

    private static final String PREFERENCE_PASSWORD_ALWAYS = "preference_password_always";

    private static FetLifeApplication instance;

    private DatabaseDefinition baseDataBaseDefinition;

    private ImageLoader imageLoader;
    private NotificationParser notificationParser;
    private FetLifeService fetLifeService;

    private String versionText;
    private int versionNumber;
    private Activity foregroundActivity;

    private String accessToken;
    private Member user;

    private EventBus eventBus;

    public static FetLifeApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Setup default instance and callbacks
        instance = this;
        registerActivityLifecycleCallbacks(new ForegroundActivityObserver());

        //Init crash logging
        Fabric.with(this, new Crashlytics());

        //SetUp preferences if needed
        applyDefaultPreferences(false);

        //Load logged in user
        user = loadLoggedInUser();
        if (user != null) {
            initUserDb();
        }

        //Init push notifications
        OneSignal.startInit(this).setNotificationOpenedHandler(new OnNotificationOpenedHandler()).init();
        OneSignal.enableNotificationsWhenActive(true);

        //Init members
        try {
            fetLifeService = new FetLifeService(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        imageLoader = new ImageLoader(this);
        notificationParser = new NotificationParser();
        eventBus = EventBus.getDefault();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText = pInfo.versionName;
            versionNumber = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionText = getString(R.string.text_unknown);
        }

    }

    public void setBaseDataBaseDefinition(DatabaseDefinition baseDataBaseDefinition) {
        this.baseDataBaseDefinition = baseDataBaseDefinition;
    }

    public DatabaseDefinition getBaseDataBaseDefinition() {
        return baseDataBaseDefinition;
    }

    private Member loadLoggedInUser() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lastNickname = getLastFromUserHistory(preferences);
        if (lastNickname == null) {
            return null;
        }
        String userAsJson = preferences.getString(FetLifeApplication.CONSTANT_PREF_KEY_ME_JSON + lastNickname, null);
        if (userAsJson == null) {
            return null;
        }
        try {
            return new ObjectMapper().readValue(userAsJson, Member.class);
        } catch (IOException e) {
            preferences.edit().remove(CONSTANT_PREF_KEY_ME_JSON + lastNickname);
            return null;
        }
    }

    public void setCurrentUser(Member user) {

        this.user = user;
        saveCurrentUser();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_VERSION,1);
            jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_NICKNAME, user.getNickname());
            jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN, user.getNotificationToken());
            OneSignal.sendTags(jsonObject);
        } catch (JSONException e) {
            //TODO: error handling
        }

        OneSignal.setSubscription(true);

        initUserDb();
    }

    public void updateCurrentUser(Member user) {
        this.user = user;
        saveCurrentUser();
    }

    private void saveCurrentUser() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        String userAsJson;
        try {
            userAsJson = user.toJsonString();
            if (!getPasswordAlwaysPreference()) {
                editor.putString(FetLifeApplication.CONSTANT_PREF_KEY_ME_JSON + user.getNickname(), userAsJson);
                addToUserHistory(user.getNickname());
            } else {
                editor.remove(FetLifeApplication.CONSTANT_PREF_KEY_ME_JSON + user.getNickname());
                deleteFromUserHistory(user.getNickname());
            }
        } catch (JsonProcessingException e) {
            editor.remove(FetLifeApplication.CONSTANT_PREF_KEY_ME_JSON + user.getNickname());
            deleteFromUserHistory(user.getNickname());
        }
        editor.apply();
    }

    private void deleteCurrentUser() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(FetLifeApplication.CONSTANT_PREF_KEY_ME_JSON + user.getNickname());
        editor.apply();
        deleteFromUserHistory(user.getNickname());
    }

    public Member getUser() {
        if (user == null) {
            loadLoggedInUser();
            if (user != null) {
                initUserDb();
            }
        }
        return user;
    }

    private void initUserDb() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int databaseVersion = preferences.getInt(CONSTANT_PREF_KEY_DB_VERSION, 0);
        if (databaseVersion < FetLifeDatabase.MIN_SUPPORTED_VERSION) {
            DeleteUserDatabase();
        }
        preferences.edit().putInt(CONSTANT_PREF_KEY_DB_VERSION, FetLifeDatabase.VERSION).apply();
        FlowManager.init(new FlowConfig.Builder(this).addDatabaseHolder(UserDatabaseHolder.class).build());
    }

    public void removeCurrentUser(boolean deleteContent) {
        if (deleteContent) {
            DeleteUserDatabase();
            deleteCurrentUser();
        }
        FlowManager.destroy();
        user = null;
    }

    private void applyDefaultPreferences(boolean forceDefaults) {
        PreferenceManager.setDefaultValues(this, R.xml.notification_preferences, forceDefaults);
    }

    private void clearPreferences() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().commit();
        applyDefaultPreferences(true);
    }

    private String getLastFromUserHistory(SharedPreferences preferences) {
        String knownUsersAsJson = preferences.getString(FetLifeApplication.CONSTANT_PREF_KEY_USERS_JSON, null);
        if (knownUsersAsJson == null) {
            return null;
        }
        String lastNickname = null;
        try {
            Users knownUsers = new ObjectMapper().readValue(knownUsersAsJson, Users.class);
            List<String> nicknames = knownUsers.getNicknames();
            if (nicknames != null && !nicknames.isEmpty()) {
                lastNickname = nicknames.get(nicknames.size()-1);
            }
            return lastNickname;
        } catch (IOException e) {
            throw new RuntimeException("Invalid user history");
        }
    }

    private void deleteFromUserHistory(String nickname) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String knownUsersAsJson = preferences.getString(FetLifeApplication.CONSTANT_PREF_KEY_USERS_JSON, null);
        if (knownUsersAsJson != null) {
            try {
                Users knownUsers = new ObjectMapper().readValue(knownUsersAsJson, Users.class);
                List<String> nicknames = knownUsers.getNicknames();
                if (nicknames != null && !nicknames.isEmpty()) {
                    int userLoginHistoryId = Collections.binarySearch(nicknames, nickname);
                    if (userLoginHistoryId >= 0) {
                        nicknames.remove(userLoginHistoryId);
                        knownUsers.setNicknames(nicknames);
                        preferences.edit().putString(CONSTANT_PREF_KEY_USERS_JSON, new ObjectMapper().writeValueAsString(knownUsers)).apply();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Invalid user history");
            }
        }
    }

    private void addToUserHistory(String nickname) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String knownUsersAsJson = preferences.getString(FetLifeApplication.CONSTANT_PREF_KEY_USERS_JSON, null);
        Users knownUsers = null;
        List<String> nicknames = null;
        if (knownUsersAsJson != null) {
            try {
                knownUsers = new ObjectMapper().readValue(knownUsersAsJson, Users.class);
                nicknames = knownUsers.getNicknames();
                if (nicknames != null && !nicknames.isEmpty()) {
                    int userLoginHistoryId = Collections.binarySearch(nicknames, nickname);
                    if (userLoginHistoryId >= 0) {
                        nicknames.remove(userLoginHistoryId);
                    }
                }
            } catch (IOException e) {
                //skip
            }
        }
        if (knownUsers == null || nicknames == null) {
            knownUsers = new Users();
            nicknames = new ArrayList<>();
        }
        if (nicknames == null) {
            nicknames = new ArrayList<>();
        }
        nicknames.add(nickname);
        knownUsers.setNicknames(nicknames);
        try {
            preferences.edit().putString(CONSTANT_PREF_KEY_USERS_JSON, new ObjectMapper().writeValueAsString(knownUsers)).apply();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Invalid user history");
        }
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

    public void DeleteUserDatabase() {
        deleteDatabase(FetLifeDatabase.NAME + "_" + user.getNickname() +".db");

        //TODO: keep a bit for legacy purposes, but delete later
        deleteDatabase(FetLifeDatabase.NAME + ".db");
        //DBFlow library uses .db suffix, but they mentioned they might going to change this in the future
        deleteDatabase(FetLifeDatabase.NAME);
    }

    public String getVersionText() {
        return versionText;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setPasswordAlwaysPreference(boolean checked) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(PREFERENCE_PASSWORD_ALWAYS, checked).apply();
    }
    public boolean getPasswordAlwaysPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(PREFERENCE_PASSWORD_ALWAYS, true);
    }

    public void doSoftLogout() {
        setAccessToken(null);
        removeCurrentUser(false);
    }

    public void doHardLogout() {

        setAccessToken(null);

        //OneSignal.setSubscription(false);
        //clearPreferences();

        if (getUser() != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_VERSION, 1);
                jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_NICKNAME, getUser().getNickname());
                jsonObject.put(FetLifeApplication.CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN, "");
                OneSignal.sendTags(jsonObject);

                String[] tags = new String[]{
                        FetLifeApplication.CONSTANT_ONESIGNAL_TAG_VERSION,
                        FetLifeApplication.CONSTANT_ONESIGNAL_TAG_NICKNAME,
                        FetLifeApplication.CONSTANT_ONESIGNAL_TAG_MEMBER_TOKEN
                };
                OneSignal.deleteTags(Arrays.asList(tags));

            } catch (JSONException e) {
                //TODO: error handling
            }

            removeCurrentUser(true);
        }
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
            if (foregroundActivity == null && !activity.isChangingConfigurations() && getPasswordAlwaysPreference()) {
                doSoftLogout();
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

