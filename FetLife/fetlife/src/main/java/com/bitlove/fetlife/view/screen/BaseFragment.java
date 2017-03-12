package com.bitlove.fetlife.view.screen;

import android.support.v4.app.Fragment;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.view.screen.component.ActivityComponent;

public class BaseFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        FetLifeApplication.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        FetLifeApplication.getInstance().getEventBus().unregister(this);
    }

}
