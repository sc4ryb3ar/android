package com.bitlove.fetlife.view.activity.resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bitlove.fetlife.model.pojos.Story;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.component.MenuActivityComponent;
import com.bitlove.fetlife.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;

public class FeedActivity extends ResourceListActivity<Story> implements MenuActivityComponent.MenuActivityCallBack {

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, FeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        return new FeedRecyclerAdapter(getFetLifeApplication());
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
}
