package com.bitlove.fetlife.view.screen.resource;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
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
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Video;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.DateUtil;
import com.bitlove.fetlife.util.StringUtil;
import com.bitlove.fetlife.util.UrlUtil;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.widget.FlingBehavior;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

public class EventActivity extends ResourceActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PROFILE_MENU_HITREC_PADDING = 200;

    private static final String EXTRA_EVENTID = "EXTRA_EVENTID";
    private TextView eventSubTitle;
    private TextView eventTitle;

    private ViewPager viewPager;
    private Event event;

    public static void startActivity(BaseActivity baseActivity, String eventId) {
        Intent intent = new Intent(baseActivity, EventActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(EXTRA_EVENTID, eventId);
        baseActivity.startActivity(intent);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        String eventId = getIntent().getStringExtra(EXTRA_EVENTID);
        event = Event.loadEvent(eventId);

        setTitle(event.getName());
        eventSubTitle = (TextView) findViewById(R.id.event_subtitle);
        eventSubTitle.setText(event.getTagline());

        setEventDetails(event);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return EventInfoFragment.newInstance(event.getId());
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
                        return getString(R.string.title_fragment_event_details);
                    default:
                        return null;
                }
            }
        });

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        params.setBehavior(new FlingBehavior());
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        getSupportActionBar().setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        TextView headerTitle = (TextView) findViewById(R.id.event_title);
        toolbarTitle.setText(title);
        headerTitle.setText(title);
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
        if (isRelatedCall(serviceCallFinishedEvent.getServiceCallAction(), serviceCallFinishedEvent.getParams()) && !isRelatedCall(FetLifeApiIntentService.getActionInProgress(), FetLifeApiIntentService.getInProgressActionParams())) {
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
        String eventId = event.getId();
        if (params != null && params.length > 0 && eventId != null && !eventId.equals(params[0])) {
            return false;
        }
//        if (FetLifeApiIntentService.ACTION_APICALL_EVENT.equals(serviceCallAction)) {
//            return true;
//        }
        return false;
    }

    public void onAddEventToCalendar(View v) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, DateUtil.parseDate(event.getStartDateTime(),true));
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, DateUtil.parseDate(event.getEndDateTime(),true));
        String eventName = event.getName();
        if (!TextUtils.isEmpty(event.getTagline())) {
            eventName += "\n" + event.getTagline();
        }
        intent.putExtra(CalendarContract.Events.TITLE, eventName);
        String eventDescription = event.getDescription();
        if (!TextUtils.isEmpty(event.getDressCode())) {
            eventDescription += "\n\n" + getString(R.string.text_event_header_dresscodes) + " " + event.getDressCode();
        }
        if (!TextUtils.isEmpty(event.getCost())) {
            eventDescription += "\n\n" + getString(R.string.text_event_header_cost) + " " + event.getCost();
        }
        intent.putExtra(CalendarContract.Events.DESCRIPTION,eventDescription);
        String eventLocation = "";
        if (!TextUtils.isEmpty(event.getLocation())) {
            eventLocation = event.getLocation();
        }
        if (!TextUtils.isEmpty(eventLocation) && !TextUtils.isEmpty(event.getAddress())) {
            eventLocation += ", " + event.getAddress();
        }
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation);
        startActivity(intent);
    }

    public void onViewEvent(View v) {
        UrlUtil.openUrl(this,event.getUrl());
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

    private static final float PERCENTAGE_TO_SHOW_TITLE_DETAILS = 0.7f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final long ALPHA_ANIMATIONS_DELAY = 200l;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        setToolbarVisibility(appBarLayout, findViewById(R.id.toolbar_title), findViewById(R.id.toolbar_image), percentage);
    }

    private boolean isTitleVisible = false;

    private void setToolbarVisibility(AppBarLayout appBarLayout, View title, View image, float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_DETAILS) {
            if (!isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.VISIBLE);
                startAlphaAnimation(image, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.VISIBLE);
                ((SimpleDraweeView) image).setImageURI((String) image.getTag());
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
