package com.bitlove.fetlife.view.screen.resource.groups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.view.screen.resource.LoadFragment;
import com.bitlove.fetlife.view.screen.resource.ResourceActivity;
import com.bitlove.fetlife.view.screen.resource.profile.GroupsFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;

public class GroupsActivity extends ResourceActivity implements MenuActivityComponent.MenuActivityCallBack {

    private ViewPager viewPager;
    private WeakReference<Fragment> currentFragmentReference;

    public static void startActivity(BaseActivity baseActivity,boolean newTask) {
        baseActivity.startActivity(createIntent(baseActivity,newTask));
    }
    public static Intent createIntent(Context context, boolean newTask) {
        Intent intent = new Intent(context, GroupsActivity.class);
        if (!newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    @Override
    public boolean finishAtMenuNavigation() {
        return true;
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                currentFragmentReference = object != null ? new WeakReference<Fragment>((Fragment) object) : null;
                super.setPrimaryItem(container, position, object);
            }

            @Override
            public Fragment getItem(int position) {
                Member user = getFetLifeApplication().getUserSessionManager().getCurrentUser();
                if (user == null) {
                    return null;
                }
                String userId = user.getId();
                switch (position) {
                    case 0:
                        return GroupsFragment.newInstance(userId);
                    case 1:
                        return SearchGroupFragment.newInstance(null);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.title_fragment_groups);
                    case 1:
                        return getString(R.string.title_fragment_group_serach);
                    default:
                        return null;
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (isRelatedCall(serviceCallStartedEvent.getServiceCallAction(), serviceCallStartedEvent.getParams())) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (isRelatedCall(serviceCallFinishedEvent.getServiceCallAction(),serviceCallFinishedEvent.getParams()) && !isRelatedCall(FetLifeApiIntentService.getActionInProgress(),FetLifeApiIntentService.getInProgressActionParams())) {
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (isRelatedCall(serviceCallFailedEvent.getServiceCallAction(), serviceCallFailedEvent.getParams())) {
            dismissProgress();
        }
    }

    private boolean isRelatedCall(String serviceCallAction, String[] params) {
        Member currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_SEARCH_GROUP.equals(serviceCallAction)) {
            return true;
        }
        if (params != null && params.length > 0 && currentUser.getId() != null && !currentUser.getId().equals(params[0])) {
            return false;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_GROUPS.equals(serviceCallAction)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResourceStart() {
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_groups);
    }

    @Override
    public void showProgress() {
        super.showProgress();
        Fragment page = getCurrentFragment();
        if (page != null && page instanceof LoadFragment) {
            ((LoadFragment)page).showProgress();
        }
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
        Fragment page = getCurrentFragment();
        if (page != null && page instanceof LoadFragment) {
            ((LoadFragment)page).dismissProgress();
        }
    }

    private Fragment getCurrentFragment() {
        if (currentFragmentReference == null) {
            return null;
        }
        return currentFragmentReference.get();
    }
}
