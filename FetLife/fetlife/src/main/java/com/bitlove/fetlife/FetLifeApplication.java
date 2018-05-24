package com.bitlove.fetlife;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.widget.Toast;

import com.bitlove.fetlife.inbound.OnNotificationOpenedHandler;
import com.bitlove.fetlife.model.api.FetLifeService;
import com.bitlove.fetlife.model.api.GitHubService;
import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.model.inmemory.InMemoryStorage;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.notification.NotificationParser;
import com.bitlove.fetlife.session.UserSessionManager;
import com.bitlove.fetlife.util.FileUtil;
import com.bitlove.fetlife.view.screen.resource.ResourceListActivity;
import com.bitlove.fetlife.view.screen.standalone.LoginActivity;
import com.crashlytics.android.Crashlytics;
import com.facebook.cache.common.CacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.AlgorithmParameters;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.fabric.sdk.android.Fabric;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Main Application class. The lifecycle of the object of this class is the same as the App itself
 */
public class FetLifeApplication extends MultiDexApplication {

    private static final String IMAGE_TOKEN_MIDFIX = "?token=";

    /**
     * Preference key for version number for last upgrade was executed.
     * Upgrade for certain version might be executed to ensure backward compatibility
     */
    private static final String APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED = "APP_PREF_KEY_INT_VERSION_UPGRADE_EXECUTED";

    /**
     * Logout delay in case of additional task started that is outside of the App (like photo App for taking a photo)
     * We do not want to log out the user right away in this case
     */
    private static final long WAITING_FOR_RESULT_LOGOUT_DELAY_MILLIS = 60 * 1000;

    private static final String PREFIX_FILE_DB = "db_";

    //****
    //App singleton behaviour to make it accessible where dependency injection is not possible
    //****

    private static FetLifeApplication instance;

    private String lastToastMessage;
    private long lastToastTime;

    private static final long MIN_TOAST_TRASHOLD = 5*1000l;

    public static FetLifeApplication getInstance() {
        return instance;
    }

    /**
     * App version info fields
     */
    private String versionText;
    private int versionNumber;

    /**
     * Currently displayed Activity if there is any
     */
    private Activity foregroundActivity;

    //****
    //Service objects
    //****

    private FetLifeService fetLifeService;
    private NotificationParser notificationParser;
    private EventBus eventBus;
    private UserSessionManager userSessionManager;
    private InMemoryStorage inMemoryStorage;

    private GitHubService gitHubService;

    @Override
    public void onCreate() {
        super.onCreate();

        //See: https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

//        if (BuildConfig.DEBUG) {
//            Debug.waitForDebugger();
//        }

        //Setup default instance and callbacks
        instance = this;

        //Setup App version info
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionText = pInfo.versionName;
            versionNumber = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionText = getString(R.string.text_unknown);
        }

        //Init Fresco image library
        initFrescoImageLibrary();

        //Init crash logging
        Fabric.with(this, new Crashlytics());

        PendingIntent restartIntent = PendingIntent.getActivity(this,42, LoginActivity.createIntent(this,getString(R.string.error_session_invalid)),PendingIntent.FLAG_ONE_SHOT);
        Thread.setDefaultUncaughtExceptionHandler(new FetLifeUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(),restartIntent));

        //Init push notifications
        OneSignal.startInit(this).inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification).setNotificationOpenedHandler(new OnNotificationOpenedHandler()).init();

        //Register activity call back to keep track of currently displayed Activity
        registerActivityLifecycleCallbacks(new ForegroundActivityObserver());

        //Init user session manager
        userSessionManager = new UserSessionManager(this);
        userSessionManager.init();

        //Init service members
        try {
            fetLifeService = new FetLifeService(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            gitHubService = new GitHubService(this);
        } catch (Exception e) {
            gitHubService = null;
        }

        notificationParser = new NotificationParser();
        eventBus = EventBus.getDefault();
        inMemoryStorage = new InMemoryStorage();
    }

    private void initFrescoImageLibrary() {

        final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        if (!chain.request().url().host().endsWith("fetlife.com")) {
                            return chain.proceed(chain.request());
                        }
                        final Request.Builder requestBuilder = chain.request().newBuilder();
                        requestBuilder.addHeader("Referer", "fetlife.com");
                        return chain.proceed(requestBuilder.build());
                    }
                });

        ImagePipelineConfig imagePipelineConfig = OkHttpImagePipelineConfigFactory.newBuilder(this,okHttpClientBuilder.build()).setCacheKeyFactory(new CacheKeyFactory() {
            @Override
            public CacheKey getBitmapCacheKey(ImageRequest request, Object callerContext) {
                Uri uri = request.getSourceUri();
                return getCacheKey(uri);
            }

            @Override
            public CacheKey getPostprocessedBitmapCacheKey(ImageRequest request, Object callerContext) {
                Uri uri = request.getSourceUri();
                return getCacheKey(uri);
            }

            @Override
            public CacheKey getEncodedCacheKey(ImageRequest request, Object callerContext) {
                Uri uri = request.getSourceUri();
                return getCacheKey(uri);
            }

            @Override
            public CacheKey getEncodedCacheKey(ImageRequest request, Uri sourceUri, Object callerContext) {
                return getCacheKey(sourceUri);
            }

            private CacheKey getCacheKey(Uri uri) {
                String imageUrl = uri.toString();
                final String cacheUrl;

                String[] imageUrlParts = imageUrl.split(Pattern.quote(IMAGE_TOKEN_MIDFIX));
                if (imageUrlParts.length >= 2) {
                    cacheUrl = imageUrlParts[0];
                    String token = imageUrlParts[1];
                } else {
                    cacheUrl = imageUrl;
                }

                CacheKey cacheKey = new FrescoTokenLessCacheKey(cacheUrl);
                return cacheKey;

            }
        }).build();

        Fresco.initialize(this, imagePipelineConfig);
    }


    static class FrescoTokenLessCacheKey implements CacheKey {

        final String cacheUrl;

        FrescoTokenLessCacheKey(String cacheUrl) {
            this.cacheUrl = cacheUrl;
        }

        @Override
        public int hashCode() {
            return cacheUrl.hashCode();
        }

        @Override
        public boolean containsUri(Uri uri) {
            return uri.toString().startsWith(cacheUrl);
        }

        @Override
        public String getUriString() {
            return cacheUrl;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof FrescoTokenLessCacheKey) {
                FrescoTokenLessCacheKey otherKey = (FrescoTokenLessCacheKey) obj;
                return cacheUrl.equals(otherKey.cacheUrl);
            }
            return super.equals(obj);
        }

        @Override
        public String toString() {
            return cacheUrl;
        }
    }

    //****
    //Displaying toast messages
    //****

    public void showToast(final int resourceId) {
        showToast(getResources().getString(resourceId));
    }

    public void showToast(final String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public void showLongToast(final int resourceId) {
        showLongToast(getResources().getString(resourceId));
    }

    public void showLongToast(final String text) {
        showToast(text, Toast.LENGTH_LONG);
    }

    private void showToast(final String text, final int length) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (text.equals(lastToastMessage) && System.currentTimeMillis() - lastToastTime < MIN_TOAST_TRASHOLD) {
                    return;
                }
                lastToastMessage = text;
                lastToastTime = System.currentTimeMillis();
                Toast.makeText(FetLifeApplication.this, text, length).show();
            }
        });
    }

    //****
    //Getter and helper methods for App foreground state
    //****

    public Activity getForegroundActivity() {
        return foregroundActivity;
    }

    public synchronized void setForegroundActivity(Activity foregroundActivity) {
        synchronized (userSessionManager) {
            this.foregroundActivity = foregroundActivity;
        }
    }

    public boolean isAppInForeground() {
        synchronized (userSessionManager) {
            return foregroundActivity != null;
        }
    }

    //****
    //Getters for service classes
    //****

    public InMemoryStorage getInMemoryStorage() {
        return inMemoryStorage;
    }

    public UserSessionManager getUserSessionManager() {
        return userSessionManager;
    }

    public FetLifeService getFetLifeService() {
        return fetLifeService;
    }

    public NotificationParser getNotificationParser() {
        return notificationParser;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public GitHubService getGitHubService() {
        return gitHubService;
    }

    //****
    //Getters for App version info
    //****

    public String getVersionText() {
        return versionText;
    }

    public int getVersionNumber() {
        return versionNumber;
    }


    //****
    //Version upgrade method to ensure backward compatibility
    //****

    public void deleteAllDatabases() {
        try {
            FileUtil.deleteDir(getDatabasePath(FetLifeDatabase.NAME + ".db").getParentFile());
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    //****
    //Class to help monitoring Activity State
    //****

    private class ForegroundActivityObserver implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (!isAppInForeground() || foregroundActivity instanceof LoginActivity) {
                FetLifeApiIntentService.startPendingCalls(FetLifeApplication.this);
            }
            setForegroundActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (getForegroundActivity() == activity) {
                setForegroundActivity(null);
            }

            boolean isWaitingForResult = isWaitingForResult(activity);
            //Check if the new Screen is already displayed so the App is still in the foreground
            //Check if the Activity is topped due to configuration change like device rotation
            //Check if we started an external task (like taking photo) for that we should wait and keep the user logged in
            if (!isAppInForeground() && !activity.isChangingConfigurations() && !isWaitingForResult) {
                //If none of the above cases happen to be true log out the user in case (s)he selected to be logged out always
                if (userSessionManager.getCurrentUser() != null && !userSessionManager.keepUserSignedIn()) {
                    userSessionManager.onUserLogOut();
                }
            } else if(isWaitingForResult) {
                //If we are waiting for an external task to be finished, start a delayed log out
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (userSessionManager) {
                            //After delay happened make sure if the user is still need to be logged out
                            //Check if App is still displayed
                            //Check if the user is not already logged out
                            //Check if the user wants us to log her/him out in case of leaving the app
                            if (!isAppInForeground() && userSessionManager.getCurrentUser() != null && !userSessionManager.keepUserSignedIn()) {
                                userSessionManager.onUserLogOut();
                            }
                        }
                    }
                }, WAITING_FOR_RESULT_LOGOUT_DELAY_MILLIS);
            }
        }

        private boolean isWaitingForResult(Activity activity) {
            if (activity instanceof ResourceListActivity) {
                return ((ResourceListActivity)activity).isWaitingForResult();
            }
            return false;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

}

