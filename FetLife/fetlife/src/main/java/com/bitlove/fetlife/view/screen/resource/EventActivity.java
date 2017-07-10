package com.bitlove.fetlife.view.screen.resource;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.db.FollowRequest;
import com.bitlove.fetlife.model.pojos.fetlife.db.RelationReference;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.FriendRequest;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.DateUtil;
import com.bitlove.fetlife.util.ViewUtil;
import com.bitlove.fetlife.view.dialog.ProfileConfirmationDialog;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.profile.AboutFragment;
import com.bitlove.fetlife.view.screen.resource.profile.ActivityFeedFragment;
import com.bitlove.fetlife.view.screen.resource.profile.BasicInfoFragment;
import com.bitlove.fetlife.view.screen.resource.profile.PicturesFragment;
import com.bitlove.fetlife.view.screen.resource.profile.RelationsFragment;
import com.bitlove.fetlife.view.screen.resource.profile.StatusesFragment;
import com.bitlove.fetlife.view.screen.resource.profile.VideosFragment;
import com.bitlove.fetlife.view.widget.FlingBehavior;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;

public class EventActivity extends ResourceActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PROFILE_MENU_HITREC_PADDING = 200;

    private static final String EXTRA_EVENTID = "EXTRA_EVENTID";
    private TextView eventSubTitle;
    private TextView eventTitle;

    private ViewPager viewPager;
    private String eventId;

    public static void startActivity(BaseActivity baseActivity, String eventId) {
        Intent intent = new Intent(baseActivity, EventActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(EXTRA_EVENTID,eventId);
        baseActivity.startActivity(intent);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        eventId = getIntent().getStringExtra(EXTRA_EVENTID);
        Event event = Event.loadEvent(eventId);

        eventTitle = (TextView) findViewById(R.id.profile_nickname);
        eventSubTitle = (TextView) findViewById(R.id.profile_meta);

        setEventDetails(event);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return EventInfoFragment.newInstance(eventId);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getString(R.string.title_fragment_profile_info);
                    default:
                        return null;
                }
            }
        });

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)appBarLayout.getLayoutParams();
        params.setBehavior(new FlingBehavior());
        appBarLayout.addOnOffsetChangedListener(this);
    }

    private void setEventDetails(Event event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (isRelatedCall(serviceCallStartedEvent.getServiceCallAction(), serviceCallStartedEvent.getParams())) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
//        if (serviceCallFinishedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_EVENT)) {
//            final String eventId = getIntent().getStringExtra(EXTRA_EVENTID);
//            Event event = Event.loadEvent(eventId);
//            setEventDetails(event);
//        }
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
        if (params != null && params.length > 0 && eventId != null && !eventId.equals(params[0])) {
            return false;
        }
//        if (FetLifeApiIntentService.ACTION_APICALL_EVENT.equals(serviceCallAction)) {
//            return true;
//        }
        return false;
    }

    private void onMenuIconMessage() {
    }

    private void onMenuIconView() {
    }

    private void onMenuIconFriend() {
    }

    private void onMenuIconFollow() {
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_event);
    }

    private static final float PERCENTAGE_TO_SHOW_TITLE_DETAILS = 0.8f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final long ALPHA_ANIMATIONS_DELAY = 400l;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        setToolbarVisibility(appBarLayout, findViewById(R.id.toolbar_title), findViewById(R.id.toolbar_image), percentage);
    }

    private boolean isTitleVisible = false;

    private void setToolbarVisibility(AppBarLayout appBarLayout, View title, View image, float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_DETAILS) {
            if(!isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.VISIBLE);
                startAlphaAnimation(image, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.VISIBLE);
                ((SimpleDraweeView)image).setImageURI((String)image.getTag());
                isTitleVisible = true;
            }
        } else {
            if (isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.INVISIBLE);
                startAlphaAnimation(image, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.INVISIBLE);
                isTitleVisible = false;
            }
        }
    }

    public static void startAlphaAnimation(final View v, long duration, long delay, final int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setStartOffset(delay);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

}
