package com.bitlove.fetlife;

import android.database.sqlite.SQLiteReadOnlyDatabaseException;
import android.os.Looper;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;

public class FetLifeUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    FetLifeUncaughtExceptionHandler(Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
        this.defaultUncaughtExceptionHandler = defaultUncaughtExceptionHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (Looper.getMainLooper().getThread() != thread) {
            if (throwable instanceof InvalidDBConfiguration | throwable instanceof SQLiteReadOnlyDatabaseException) {
                if (FetLifeApplication.getInstance().getUserSessionManager().getCurrentUser() == null) {
                    Crashlytics.logException(new Exception("DB closed",throwable));
                    //Duck exception DB is closed before background thread finished its job
                    System.exit(2);
                }
            }
        }

        defaultUncaughtExceptionHandler.uncaughtException(thread,throwable);
    }
}
