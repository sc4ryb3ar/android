package com.bitlove.fetlife.view.screen.resource.groups;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.GroupMessageSendFailedEvent;
import com.bitlove.fetlife.event.GroupMessageSendSucceededEvent;
import com.bitlove.fetlife.event.MessageSendFailedEvent;
import com.bitlove.fetlife.event.MessageSendSucceededEvent;
//import com.bitlove.fetlife.event.NewGroupPostEvent;
import com.bitlove.fetlife.event.NewGroupMessageEvent;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupComment;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.GroupPost_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Message;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.MessageDuplicationDebugUtil;
import com.bitlove.fetlife.view.adapter.GroupMembersRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.GroupMessagesRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.GroupsRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.MessagesRecyclerAdapter;
import com.bitlove.fetlife.view.screen.resource.ResourceActivity;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupMessagesActivity extends ResourceActivity
        implements NavigationView.OnNavigationItemSelectedListener, GroupMessagesRecyclerAdapter.GroupMessageClickListener {

    private static final String EXTRA_GROUP_ID = "com.bitlove.fetlife.extra.group_id";
    private static final String EXTRA_GROUP_DISUCSSION_ID = "com.bitlove.fetlife.extra.groupDiscussion_id";
    private static final String EXTRA_DISCUSSION_TITLE = "com.bitlove.fetlife.extra.groupDiscussion_title";
    private static final String EXTRA_AVATAR_RESOURCE_URL = "com.bitlove.fetlife.extra.avatar_resource_url";

    private GroupMessagesRecyclerAdapter messagesAdapter;

    private String groupDiscussionId;
    private String avatarUrl;
    private String memberId;

    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout;
    protected View inputIcon;
    protected EditText textInput;
    private String groupId;
    private GroupPost groupDiscussion;

    public static void startActivity(Context context, String groupId, String groupDiscussionId, String title, String avatarResourceUrl, boolean newTask) {
        context.startActivity(createIntent(context, groupId, groupDiscussionId, title, avatarResourceUrl, newTask));
    }

    public static Intent createIntent(Context context, String groupId, String groupDiscussionId, String title, String avatarResourceUrl, boolean newTask) {
        Intent intent = new Intent(context, GroupMessagesActivity.class);
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra(EXTRA_GROUP_ID, groupId);
        intent.putExtra(EXTRA_GROUP_DISUCSSION_ID, groupDiscussionId);
        intent.putExtra(EXTRA_DISCUSSION_TITLE, title);
        intent.putExtra(EXTRA_AVATAR_RESOURCE_URL, avatarResourceUrl);
        return intent;
    }

    public String getGroupDiscussionId() {
        return groupDiscussionId;
    }

    public String getGroupId() {
        return groupId;
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.coordinator_resource_default);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

        findViewById(R.id.text_preview).setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        setGroupPost(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        setGroupPost(intent);
    }

    @Override
    public void onMemberClick(String memberId) {
        ProfileActivity.startActivity(this,memberId);
    }

    @Override
    public void onMessageMetaClicked(String meta) {
        if (!textInput.getText().toString().endsWith(meta)) {
            textInput.append(meta);
        }
    }

    @Override
    public void onRequestPageClick(int page) {
        messagesAdapter.refresh();
        startResourceCall(getPageCount(),page);
    }

    private void setGroupPost(Intent intent) {

        groupId = intent.getStringExtra(EXTRA_GROUP_ID);
        groupDiscussionId = intent.getStringExtra(EXTRA_GROUP_DISUCSSION_ID);
        String groupDiscussionTitle = intent.getStringExtra(EXTRA_DISCUSSION_TITLE);
        avatarUrl = intent.getStringExtra(EXTRA_AVATAR_RESOURCE_URL);
        memberId = null;

        messagesAdapter = new GroupMessagesRecyclerAdapter(groupId,groupDiscussionId,this);
        groupDiscussion = messagesAdapter.getGroupPost();
        if (groupDiscussion != null) {
            String draftMessage = groupDiscussion.getDraftMessage();
            if (draftMessage != null) {
                textInput.append(draftMessage);
            }
            if (avatarUrl == null) {
                avatarUrl = groupDiscussion.getAvatarLink();
            }
            memberId = groupDiscussion.getMemberId();
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
                    ProfileActivity.startActivity(GroupMessagesActivity.this,memberId);
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
        setTitle(groupDiscussionTitle);

        recyclerView.setAdapter(messagesAdapter);
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

        messagesAdapter.refresh();

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
//        {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
//            {
//                if (dy < 0) {
//                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
//
//                    if (firstVisibleItem == 0 && requestedItems <= linearLayoutManager.getItemCount()) {
//                        requestedItems += getPageCount();
//                        startResourceCall(getPageCount(), ++requestedPage);
//                    }
//                }
//            }
//        });

        showProgress();
        startResourceCall(getPageCount(), 1);
    }

    private int getPageCount() {
        return GroupMessagesRecyclerAdapter.ITEM_PER_PAGE;
    }

    private void startResourceCall(int pageCount, int requestedPage) {
        FetLifeApiIntentService.startApiCall(GroupMessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES, groupId, groupDiscussionId, Integer.toString(pageCount), Integer.toString(requestedPage));
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Workaround for Android OS crash issue
        if (recyclerView != null) {
            recyclerView.stopScroll();
        }

        GroupPost groupDiscussion = messagesAdapter.getGroupPost();
        if (groupDiscussion != null) {
            groupDiscussion.setDraftMessage(textInput.getText().toString());
            try {
                groupDiscussion.save();
            } catch (InvalidDBConfiguration idbce) {
                Crashlytics.logException(idbce);
            }
        } else {
            Crashlytics.logException(new Exception("Draft Message could not be saved : GroupPost is bull"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES) {
            String[] params = serviceCallFinishedEvent.getParams();
            final String groupId = params[0];
            final String groupDiscussionId = params[1];
            if (this.groupId.equals(groupId) && this.groupDiscussionId.equals(groupDiscussionId)) {
                messagesAdapter.refresh();
                dismissProgress();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessagesCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES) {
            String[] params = serviceCallFailedEvent.getParams();
            final String groupId = params[0];
            final String groupDiscussionId = params[1];
            if (this.groupId.equals(groupId) && this.groupDiscussionId.equals(groupDiscussionId)) {
                //TODO: solve setting this value false only if appropriate message call is failed (otherwise same call can be triggered twice)
                messagesAdapter.refresh();
                dismissProgress();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageCallStarted(ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent.getServiceCallAction() == FetLifeApiIntentService.ACTION_APICALL_GROUP_MESSAGES) {
            showProgress();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageArrived(NewGroupMessageEvent newMessageEvent) {
        //TODO remove temporary call solution
        startResourceCall(getPageCount(),1);
//        if (!groupDiscussionId.equals(newMessageEvent.getGroupDiscussionId()) || !groupId.equals(newMessageEvent.getGroupId())) {
//            //TODO: display (snackbar?) notification
//        } else {
//            messagesAdapter.refresh();
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageSent(GroupMessageSendSucceededEvent messageSendSucceededEvent) {
        if (groupId.equals(messageSendSucceededEvent.getGroupId()) && groupDiscussionId.equals(messageSendSucceededEvent.getGroupPostId())) {
            messagesAdapter.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessageSendFailed(GroupMessageSendFailedEvent messageSendFailedEvent) {
        if (groupId.equals(messageSendFailedEvent.getGroupId()) && groupDiscussionId.equals(messageSendFailedEvent.getGroupPostId())) {
            messagesAdapter.refresh();
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onNewGroupPost(NewGroupPostEvent newGroupPostEvent) {
//        if (newGroupPostEvent.getLocalGroupPostId().equals(groupDiscussionId)) {
//            Intent intent = getIntent();
//            intent.putExtra(EXTRA_CONVERSATION_ID, newGroupPostEvent.getGroupPostId());
//            onNewIntent(intent);
//        } else {
//        }
//    }

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

        long messageDate = System.currentTimeMillis();
        if (MessageDuplicationDebugUtil.checkTypedMessage(currentUser.getId(),text) && messageDate - lastSendButtonClickTime < SEND_BUTTON_CLICK_THRESHOLD) {
            return;
        }
        lastSendButtonClickTime = System.currentTimeMillis();

        textInput.setText("");

        GroupComment message = new GroupComment();
        message.setPending(true);
        message.setDate(System.currentTimeMillis());
        message.setClientId(UUID.randomUUID().toString());
        message.setGroupId(groupId);
        message.setGroupPostId(groupDiscussionId);
        message.setBody(text.trim());
        message.setSenderId(currentUser.getId());
        message.setSenderNickname(currentUser.getNickname());
        message.save();

        FetLifeApiIntentService.startApiCall(GroupMessagesActivity.this, FetLifeApiIntentService.ACTION_APICALL_SEND_GROUP_MESSAGES);

        GroupPost groupDiscussion = messagesAdapter.getGroupPost();
        if (groupDiscussion != null) {
            groupDiscussion.setDraftMessage("");
            groupDiscussion.save();

        }

        messagesAdapter.refresh();
    }
}
