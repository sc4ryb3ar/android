package com.bitlove.fetlife.view.activity.resource;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Event;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Story;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.component.MenuActivityComponent;
import com.bitlove.fetlife.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;

public class FeedActivity extends ResourceListActivity<Story> implements MenuActivityComponent.MenuActivityCallBack, FeedRecyclerAdapter.OnFeedItemClickListener {

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, FeedActivity.class);
        if (FetLifeApplication.getInstance().getUserSessionManager().getActiveUserPreferences().getBoolean(context.getString(R.string.settings_key_general_feed_as_start),false)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return intent;
    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        super.onResourceCreate(savedInstanceState);
    }

    @Override
    protected ResourceListRecyclerAdapter<Story, ?> createRecyclerAdapter(Bundle savedInstanceState) {
        return new FeedRecyclerAdapter(getFetLifeApplication(), this);
    }

    @Override
    protected String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_FEED;
    }

    @Override
    public void onItemClick(Story feedStory) {
    }

    @Override
    public void onAvatarClick(Story feedStory) {
    }

    @Override
    public void onMemberClick(Member member) {
        openUrl(member.getLink());
        String url = member.getLink();
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    @Override
    public void onFeedInnerItemClick(Story.FeedStoryType feedStoryType, String url) {
        openUrl(url);
    }

    @Override
    public void onFeedImageClick(Story.FeedStoryType feedStoryType, String url) {
        openUrl(url);
    }

    @Override
    public void onVisitItem(Object object, String url) {
        openUrl(url);
    }

    private void openUrl(String link) {
        if (link != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            startActivity(intent);
        }
    }
}
