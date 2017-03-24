package com.bitlove.fetlife.view.screen.resource.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.model.pojos.FollowRequest;
import com.bitlove.fetlife.model.pojos.FriendRequest;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.RelationReference;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.ViewUtil;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.MessagesActivity;
import com.bitlove.fetlife.view.screen.resource.ResourceActivity;
import com.bitlove.fetlife.view.widget.FlingBehavior;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ProfileActivity extends ResourceActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PROFILE_MENU_HITREC_PADDING = 200;

    private static final String EXTRA_MEMBERID = "EXTRA_MEMBERID";

    private ViewPager viewPager;
    private TextView nickNameView,metaView;
    private SimpleDraweeView avatarView,imageHeaderView,toolbarHeaderView;
    private ImageView friendIconView,followIconView,messageIconView,viewIconView;

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


        final String memberId = getIntent().getStringExtra(EXTRA_MEMBERID);
        Member member = Member.loadMember(memberId);

        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER, memberId);
        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_STATUSES, memberId, Integer.toString(ProfileFragment.PAGE_COUNT), "1");
        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURES, memberId, Integer.toString(PicturesFragment.PAGE_COUNT), "1");
        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS, memberId, Integer.toString(RelationReference.VALUE_RELATIONTYPE_FRIEND), Integer.toString(ProfileFragment.PAGE_COUNT), "1");
        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS, memberId, Integer.toString(RelationReference.VALUE_RELATIONTYPE_FOLLOWER), Integer.toString(ProfileFragment.PAGE_COUNT), "1");
        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS, memberId, Integer.toString(RelationReference.VALUE_RELATIONTYPE_FOLLOWING), Integer.toString(ProfileFragment.PAGE_COUNT), "1");
        FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEOS, memberId, Integer.toString(VideosFragment.PAGE_COUNT), "1");

        nickNameView = (TextView) findViewById(R.id.profile_nickname);
        metaView = (TextView) findViewById(R.id.profile_meta);
        avatarView = (SimpleDraweeView) findViewById(R.id.profile_avatar);
        imageHeaderView = (SimpleDraweeView) findViewById(R.id.profile_image_header);
        toolbarHeaderView = (SimpleDraweeView) findViewById(R.id.toolbar_image);
        friendIconView = (ImageView) findViewById(R.id.profile_menu_icon_friend);
        followIconView = (ImageView) findViewById(R.id.profile_menu_icon_follow);
        messageIconView = (ImageView) findViewById(R.id.profile_menu_icon_message);
        viewIconView = (ImageView) findViewById(R.id.profile_menu_icon_view);

        setMemberDetails(member);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return BasicInfoFragment.newInstance(memberId);
                    case 1:
                        return AboutFragment.newInstance(memberId);
                    case 2:
                        return StatusesFragment.newInstance(memberId);
                    case 3:
                        return PicturesFragment.newInstance(memberId);
                    case 4:
                        return VideosFragment.newInstance(memberId);
                    case 5:
                        return RelationsFragment.newInstance(memberId,RelationReference.VALUE_RELATIONTYPE_FRIEND);
                    case 6:
                        return RelationsFragment.newInstance(memberId,RelationReference.VALUE_RELATIONTYPE_FOLLOWING);
                    case 7:
                        return RelationsFragment.newInstance(memberId,RelationReference.VALUE_RELATIONTYPE_FOLLOWER);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 8;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                //TODO(profile): move to text file
                switch (position) {
                    case 0:
                        return "Basic Info";
                    case 1:
                        return "About";
                    case 2:
                        return "Statuses";
                    case 3:
                        return "Pictures";
                    case 4:
                        return "Video";
                    case 5:
                        return "Friends";
                    case 6:
                        return "Following";
                    case 7:
                        return "Followers";
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

    private void setMemberDetails(Member member) {
        if (member == null) {
            Crashlytics.logException(new Exception("Member is null"));
        }

        setTitle(member != null ? member.getNickname() : "");
        nickNameView.setText(member != null ? member.getNickname() : "");
        metaView.setText(member != null ? member.getMetaInfo() : "");
        avatarView.setImageURI(member != null ? member.getAvatarLink() : "");
        imageHeaderView.setImageURI(member != null ? member.getAvatarLink() : "");
        toolbarHeaderView.setImageURI(member != null ? member.getAvatarLink() : "");
        toolbarHeaderView.setTag(member != null ? member.getAvatarLink() : "");

        String relationWithMe = member != null ? member.getRelationWithMe() : "";
        if (relationWithMe == null) {
            relationWithMe = "";
        }
        switch (relationWithMe) {
            case Member.VALUE_FRIEND:
                friendIconView.setImageResource(R.drawable.ic_friend);
                break;
            case Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT:
            case Member.VALUE_FRIEND_REQUEST_SENT:
                friendIconView.setImageResource(R.drawable.ic_friend_sent);
                break;
            case Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING:
            case Member.VALUE_FRIEND_REQUEST_PENDING:
                friendIconView.setImageResource(R.drawable.ic_friend_pending);
                break;
            default:
                friendIconView.setImageResource(R.drawable.ic_friend_add);
                break;
        }
        friendIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuIconFriend();
            }
        });
        ViewUtil.increaseTouchArea(friendIconView,PROFILE_MENU_HITREC_PADDING);
        followIconView.setImageResource(isFollowedByMe(member) ? R.drawable.ic_following : R.drawable.ic_follow);
        followIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuIconFollow();
            }
        });
        ViewUtil.increaseTouchArea(followIconView,PROFILE_MENU_HITREC_PADDING);
        messageIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuIconMessage();
            }
        });
        ViewUtil.increaseTouchArea(messageIconView,PROFILE_MENU_HITREC_PADDING);
        viewIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMenuIconView();
            }
        });
        ViewUtil.increaseTouchArea(viewIconView,PROFILE_MENU_HITREC_PADDING);
    }

    private boolean isFriendWithMe(Member member) {
        String relationWithMe = member.getRelationWithMe();
        if (relationWithMe == null) {
            return false;
        }
        return relationWithMe.equals(Member.VALUE_FRIEND) || relationWithMe.equals(Member.VALUE_FRIEND_WITHOUT_FOLLOWING);
    }

    private boolean isFollowedByMe(Member member) {
        if (member == null) {
            return false;
        }
        String relationWithMe = member.getRelationWithMe();
        if (relationWithMe == null) {
            return false;
        }
        return relationWithMe.equals(Member.VALUE_FRIEND) || relationWithMe.equals(Member.VALUE_FOLLOWING) ||  relationWithMe.equals(Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT) ||  relationWithMe.equals(Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING) ;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResourceListCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (isRelatedCall(serviceCallStartedEvent.getServiceCallAction())) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_MEMBER)) {
            final String memberId = getIntent().getStringExtra(EXTRA_MEMBERID);
            Member member = Member.loadMember(memberId);
            setMemberDetails(member);
        }
        if (isRelatedCall(serviceCallFinishedEvent.getServiceCallAction())) {
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (isRelatedCall(serviceCallFailedEvent.getServiceCallAction())) {
            dismissProgress();
        }
    }

    private boolean isRelatedCall(String serviceCallAction) {
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_STATUSES.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURES.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_RELATIONS.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER.equals(serviceCallAction)) {
            return true;
        }
        if (FetLifeApiIntentService.ACTION_APICALL_MEMBER_VIDEOS.equals(serviceCallAction)) {
            return true;
        }
        return false;
    }

    private void onMenuIconMessage() {
        Member member = Member.loadMember(getIntent().getStringExtra(EXTRA_MEMBERID));
        if (member != null) {
            MessagesActivity.startActivity(this, Conversation.createLocalConversation(member), member.getNickname(), member.getAvatarLink(), false);
        }
    }

    private void onMenuIconView() {
        Member member = Member.loadMember(getIntent().getStringExtra(EXTRA_MEMBERID));
        if (member != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(member.getLink()));
            startActivity(intent);
        }
    }

    private void onMenuIconFriend() {
        Member member = Member.loadMember(getIntent().getStringExtra(EXTRA_MEMBERID));
        String currentRelation = member.getRelationWithMe();
        if (currentRelation == null) {
            currentRelation = "";
        }
        if (member != null && !isFriendWithMe(member)) {
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setMemberId(member.getId());
            friendRequest.setPendingState(FriendRequest.PendingState.OUTGOING);
            friendRequest.setPending(true);
            friendRequest.save();
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_PENDING_RELATIONS);
            member.setRelationWithMe(isFollowedByMe(member) ? Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT : Member.VALUE_FRIEND_REQUEST_SENT);
            member.save();
            setMemberDetails(member);
        }
        switch (currentRelation) {
            //TODO(profile) add resource texts
            case Member.VALUE_FRIEND:
                //showToast();
                break;
            case Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT:
            case Member.VALUE_FRIEND_REQUEST_SENT:
                showToast("Friend request has been already sent");
                break;
            case Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING:
            case Member.VALUE_FRIEND_REQUEST_PENDING:
                showToast("Friend request is waiting from your approval; Check Friend Requests Screen");
                break;
            default:
                showToast(getString(R.string.message_friend_request_sent,member.getNickname()));
                break;
        }

    }

    private void onMenuIconFollow() {
        Member member = Member.loadMember(getIntent().getStringExtra(EXTRA_MEMBERID));
        if (member != null && !isFollowedByMe(member) && member.isFollowable()) {
            FollowRequest followRequest = new FollowRequest();
            followRequest.setMemberId(member.getId());
            followRequest.save();
            String relationShipWithMe = member.getRelationWithMe() != null ? member.getRelationWithMe() : "";
            switch (relationShipWithMe) {
                case Member.VALUE_FRIEND_WITHOUT_FOLLOWING:
                    member.setRelationWithMe(Member.VALUE_FRIEND);
                    break;
                case Member.VALUE_FRIEND_REQUEST_PENDING:
                    member.setRelationWithMe(Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING);
                    break;
                case Member.VALUE_FRIEND_REQUEST_SENT:
                    member.setRelationWithMe(Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT);
                    break;
                default:
                    member.setRelationWithMe(Member.VALUE_FOLLOWING);
                    break;
            }
            member.mergeSave();
            setMemberDetails(member);
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_PENDING_RELATIONS);
            showToast(getString(R.string.message_follow_set,member.getNickname()));
        } else if (isFollowedByMe(member)){
            FollowRequest followRequest = new FollowRequest();
            followRequest.setMemberId(member.getId());
            followRequest.setFollow(false);
            followRequest.save();
            String relationShipWithMe = member.getRelationWithMe() != null ? member.getRelationWithMe() : "";
            switch (relationShipWithMe) {
                case Member.VALUE_FOLLOWING:
                    member.setRelationWithMe(null);
                    break;
                case Member.VALUE_FOLLOWING_FRIEND_REQUEST_PENDING:
                    member.setRelationWithMe(Member.VALUE_FRIEND_REQUEST_PENDING);
                    break;
                case Member.VALUE_FOLLOWING_FRIEND_REQUEST_SENT:
                    member.setRelationWithMe(Member.VALUE_FRIEND_REQUEST_SENT);
                    break;
                case Member.VALUE_FRIEND:
                    member.setRelationWithMe(Member.VALUE_FRIEND_WITHOUT_FOLLOWING);
                    break;
            }
            member.mergeSave();
            setMemberDetails(member);
            FetLifeApiIntentService.startApiCall(this,FetLifeApiIntentService.ACTION_APICALL_PENDING_RELATIONS);
            showToast(getString(R.string.message_unfollow_set,member.getNickname()));
        }
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
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_profile);
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
