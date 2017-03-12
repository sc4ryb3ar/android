package com.bitlove.fetlife.view.screen.resource.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.ResourceActivity;
import com.bitlove.fetlife.view.widget.NestedScrollViewBehavior;
import com.facebook.drawee.view.SimpleDraweeView;

public class ProfileActivity extends ResourceActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final String EXTRA_MEMBERID = "EXTRA_MEMBERID";

    private ViewPager viewPager;

    public static void startActivity(BaseActivity baseActivity, String memberId) {
        Intent intent = new Intent(baseActivity, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(EXTRA_MEMBERID,memberId);
        baseActivity.startActivity(intent);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        AppBarLayout scrollView = (AppBarLayout)findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)scrollView.getLayoutParams();
        params.setBehavior(new NestedScrollViewBehavior());

        final String memberId = getIntent().getStringExtra(EXTRA_MEMBERID);
        Member member = Member.loadMember(memberId);
        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER, memberId);

        setTitle(member.getNickname());
        TextView nickNameView = (TextView) findViewById(R.id.profile_nickname);
        nickNameView.setText(member.getNickname());
        TextView metaView = (TextView) findViewById(R.id.profile_meta);
        metaView.setText(member.getMetaInfo());
        SimpleDraweeView avatarView = (SimpleDraweeView) findViewById(R.id.profile_avatar);
        avatarView.setImageURI(member.getAvatarLink());
        SimpleDraweeView imageHeaderView = (SimpleDraweeView) findViewById(R.id.profile_image_header);
        imageHeaderView.setImageURI(member.getAvatarLink());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return AboutFragment.newInstance(memberId);
                    case 1:
                        return AboutFragment.newInstance(memberId);
                    case 2:
                        return AboutFragment.newInstance(memberId);
                    case 3:
                        return AboutFragment.newInstance(memberId);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "About";
                    case 1:
                        return "Statuses";
                    case 2:
                        return "Images";
                    case 3:
                        return "Friends";
                    default:
                        return null;
                }
            }
        });

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onResourceStart() {
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_resource_viewpager_content);
    }

    private static final float PERCENTAGE_TO_SHOW_TITLE_DETAILS = 0.8f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final long ALPHA_ANIMATIONS_DELAY = 400l;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        setToolbarVisibility(appBarLayout, findViewById(R.id.toolbar_title), percentage);
    }

    private boolean isTitleVisible = false;

    private void setToolbarVisibility(AppBarLayout appBarLayout, View title, float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_DETAILS) {
            if(!isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.VISIBLE);
                isTitleVisible = true;
            }
        } else {
            if (isTitleVisible) {
                startAlphaAnimation(title, ALPHA_ANIMATIONS_DURATION, ALPHA_ANIMATIONS_DELAY, View.INVISIBLE);
                isTitleVisible = false;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, long delay, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(duration);
        alphaAnimation.setStartOffset(delay);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

}
