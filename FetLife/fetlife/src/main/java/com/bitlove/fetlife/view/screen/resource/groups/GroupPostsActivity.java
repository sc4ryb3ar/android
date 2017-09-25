package com.bitlove.fetlife.view.screen.resource.groups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Group_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.json.GroupPost;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
public class GroupPostsActivity extends ResourceActivity
        implements NavigationView.OnNavigationItemSelectedListener, MenuActivityComponent.MenuActivityCallBack{

    private static final String EXTRA_GROUP_ID = "com.bitlove.fetlife.extra.group_id";
    private static final String EXTRA_GROUP_TITLE = "com.bitlove.fetlife.extra.group_title";
    private static final String EXTRA_AVATAR_RESOURCE_URL = "com.bitlove.fetlife.extra.avatar_resource_url";

    private GroupPostsRecyclerAdapter groupPostsAdapter;

    private String groupId;
    private String avatarUrl;
    private String memberId;
    private boolean oldGroupPostLoadingInProgress;

    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout;
    protected View inputIcon;
    protected EditText textInput;
    private Group group;

    public static void startActivity(Context context, String groupId, String title, String avatarResourceUrl, boolean newTask) {
        context.startActivity(createIntent(context, groupId, title, avatarResourceUrl, newTask));
    }

    public static Intent createIntent(Context context, String groupId, String title, String avatarResourceUrl, boolean newTask) {
        Intent intent = new Intent(context, GroupPostsActivity.class);
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_GROUP_TITLE, title);
        intent.putExtra(EXTRA_AVATAR_RESOURCE_URL, avatarResourceUrl);
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_resource_recycler_menu);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setEnabled(false);

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        textInput = (EditText) findViewById(R.id.text_input);
//        textInput.setFilters(new InputFilter[]{new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                  //Custom Emoji Support will go here
//        }});

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        inputLayout.setVisibility(View.VISIBLE);
        inputIcon.setVisibility(View.VISIBLE);

        setGroup(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setGroup(intent);
    }

    private void setGroup(Intent intent) {

        groupId = intent.getStringExtra(EXTRA_GROUP_ID);
        String groupTitle = intent.getStringExtra(EXTRA_GROUP_TITLE);
        avatarUrl = intent.getStringExtra(EXTRA_AVATAR_RESOURCE_URL);
        memberId = null;

        groupPostsAdapter = new GroupPostsRecyclerAdapter(groupId);
        group = groupPostsAdapter.getGroup();
        if (group != null) {
            String draftGroupPost = group.getDraftGroupPost();
            if (draftGroupPost != null) {
                textInput.append(draftGroupPost);
            }
            if (avatarUrl == null) {
                avatarUrl = group.getAvatarLink();
            }
            memberId = group.getMemberId();
        }

        if (avatarUrl != null) {
            toolBarImage.setVisibility(View.VISIBLE);
            toolBarImage.setImageURI(avatarUrl);
        } else {
            toolBarImage.setVisibility(View.GONE);
        }
        View.OnClickListener toolBarItemClickListener;
        if (memberId != null) {
            toolBarItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileActivity.startActivity(GroupPostsActivity.this,memberId);
                }
            };
        } else {
            toolBarItemClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            };
        }
        toolBarImage.setOnClickListener(toolBarItemClickListener);
        toolBarTitle.setOnClickListener(toolBarItemClickListener);
        setTitle(groupTitle);

        recyclerView.setAdapter(groupPostsAdapter);
    }

    public String getGroupId() {
        return groupId;
    }

    @Override
    protected void onResourceStart() {

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        textInput = (EditText) findViewById(R.id.text_input);
//        textInput.setFilters(new InputFilter[]{new InputFilter() {
//            @Override
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                  //Custom Emoji Support will go here
//        }});

        groupPostsAdapter.refresh();

        if (!Group.isLocal(groupId)) {

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    if(!oldGroupPostLoadingInProgress && dy < 0) {
                        int lastVisibleItem = recyclerLayoutManager.findLastVisibleItemPosition();
                        int totalItemCount = recyclerLayoutManager.getItemCount();

                        if (lastVisibleItem == (totalItemCount-1)) {
                            oldGroupPostLoadingInProgress = true;
                            //TODO: not trigger call if the old groupPosts were already triggered and there was no older groupPost
                            FetLifeApiIntentService.startApiCall(GroupPostsActivity.this, FetLifeApiIntentService.ACTION_APICALL_GROUP_POSTS, groupId, Boolean.toString(false));
                        }
                    }
                }
            });

            showProgress();
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_GROUP_POSTS, groupId);
        } else if (groupPostsAdapter.getItemCount() != 0) {
            showProgress();
            FetLifeApiIntentService.startApiCall(this, FetLifeApiIntentService.ACTION_APICALL_SEND_GROUP_POSTS, groupId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Workaround for Android OS crash issue
        if (recyclerView != null) {
            recyclerView.stopScroll();
        }

        Group group = groupPostsAdapter.getGroup();
        if (group != null) {
            group.setDraftGroupPost(textInput.getText().toString());
            try {
                group.save();
            } catch (InvalidDBConfiguration idbce) {
                Crashlytics.logException(idbce);
            }
        } else {
            Crashlytics.logException(new Exception("Draft GroupPost could not be saved : Group is bull"));
        }
    }

    @Override
    public void onBackPressed() {
        if (Group.isLocal(groupId) && groupPostsAdapter.getItemCount() == 0) {
            //TODO: consider using it in a db thread executor
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new Delete().from(Group.class).where(Group_Table.id.is(groupId)).query();
                }
            }).start();
        }
        super.onBackPressed();
    }

    private void setGroupPostsRead() {
        final List<String> params = new ArrayList<>();
        params.add(groupId);

        for (int i = 0; i < groupPostsAdapter.getItemCount(); i++) {
            GroupPost groupPost = groupPostsAdapter.getItem(i);
            if (!groupPost.getPending() && groupPost.isNewGroupPost()) {
                params.add(groupPost.getId());
            }
        }

        if (params.size() == 1) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                FetLifeApiIntentService.startApiCall(GroupPostsActivity.this.getApplicationContext(), FetLifeApiIntentService.ACTION_APICALL_SET_GROUP_POSTS_READ, params.toArray(new String[params.size()]));
            }
        }).run();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupPostsCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP_POSTS) {
            //TODO: solve setting this value false only if appropriate groupPost call is finished (otherwise same call can be triggered twice)
            oldGroupPostLoadingInProgress = false;
            groupPostsAdapter.refresh();
            setGroupPostsRead();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupPostsCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP_POSTS) {
            //TODO: solve setting this value false only if appropriate groupPost call is failed (otherwise same call can be triggered twice)
            oldGroupPostLoadingInProgress = false;
            groupPostsAdapter.refresh();
            dismissProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupPostCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP_POSTS) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewGroupPostArrived(NewGroupPostEvent newGroupPostEvent) {
        if (!groupId.equals(newGroupPostEvent.getGroupId())) {
            //TODO: display (snackbar?) notification
        } else {
            groupPostsAdapter.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewGroupPostSent(GroupPostSendSucceededEvent groupPostSendSucceededEvent) {
        if (groupId.equals(groupPostSendSucceededEvent.getGroupId())) {
            groupPostsAdapter.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewGroupPostSendFailed(GroupPostSendFailedEvent groupPostSendFailedEvent) {
        if (groupId.equals(groupPostSendFailedEvent.getGroupId())) {
            groupPostsAdapter.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewGroup(NewGroupEvent newGroupEvent) {
        if (newGroupEvent.getLocalGroupId().equals(groupId)) {
            Intent intent = getIntent();
            intent.putExtra(EXTRA_GROUP_ID, newGroupEvent.getGroupId());
            onNewIntent(intent);
        } else {
        }
    }

    private long lastSendButtonClickTime = 0l;
    private static final long SEND_BUTTON_CLICK_THRESHOLD = 700l;

    public void onSend(View v) {
        final String text = textInput.getText().toString();

        if (text == null || text.trim().length() == 0) {
            return;
        }

        Member currentUser = getFetLifeApplication().getUserSessionManager().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        long groupPostDate = System.currentTimeMillis();
        if (GroupPostDuplicationDebugUtil.checkTypedGroupPost(currentUser.getId(),text) && groupPostDate - lastSendButtonClickTime < SEND_BUTTON_CLICK_THRESHOLD) {
            return;
        }
        lastSendButtonClickTime = System.currentTimeMillis();

        textInput.setText("");

        GroupPost groupPost = new GroupPost();
        groupPost.setPending(true);
        groupPost.setDate(System.currentTimeMillis());
        groupPost.setClientId(UUID.randomUUID().toString());
        groupPost.setGroupId(groupId);
        groupPost.setBody(text.trim());
        groupPost.setSenderId(currentUser.getId());
        groupPost.setSenderNickname(currentUser.getNickname());
        groupPost.save();

        FetLifeApiIntentService.startApiCall(GroupPostsActivity.this, FetLifeApiIntentService.ACTION_APICALL_SEND_GROUP_POSTS);

        Group group = groupPostsAdapter.getGroup();
        if (group != null) {
            group.setDraftGroupPost("");
            group.save();

        }

        groupPostsAdapter.refresh();
    }

    @Override
    public boolean finishAtMenuNavigation() {
        return true;
    }
}
*/

