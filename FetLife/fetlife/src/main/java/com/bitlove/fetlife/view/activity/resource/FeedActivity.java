package com.bitlove.fetlife.view.activity.resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bitlove.fetlife.model.pojos.FeedStory;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.component.MenuActivityComponent;
import com.bitlove.fetlife.view.adapter.FeedRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;

public class FeedActivity extends ResourceListActivity<FeedStory> implements MenuActivityComponent.MenuActivityCallBack {

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, FeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
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
    protected ResourceListRecyclerAdapter<FeedStory, ?> createRecyclerAdapter(Bundle savedInstanceState) {
        return new FeedRecyclerAdapter(getFetLifeApplication());
    }

    @Override
    protected String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_FEED;
    }

    @Override
    public void onItemClick(FeedStory feedStory) {
    }

    @Override
    public void onAvatarClick(FeedStory feedStory) {
    }
}
