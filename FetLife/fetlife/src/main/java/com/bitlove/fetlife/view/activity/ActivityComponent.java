package com.bitlove.fetlife.view.activity;

import android.app.Activity;
import android.app.Application;
import android.view.MenuItem;

public abstract class ActivityComponent implements Application.ActivityLifecycleCallbacks {

    public Boolean onActivityOptionsItemSelected(BaseActivity baseActivity, MenuItem item) {
        return null;
    }

}
