package com.bitlove.fetlife.view.screen.resource;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.view.screen.BaseActivity;

public class TurboLinkViewActivity extends ResourceActivity implements TurbolinksAdapter {

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_turbolinkview2);
    }

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        getSupportActionBar().setTitle(R.string.title_activity_webview_support);

        TurbolinksView turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit("https://fetlife.com/support");
    }

    @Override
    protected void onResourceStart() {
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onReceivedError(int errorCode) {

    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {

    }

    @Override
    public void visitCompleted() {

    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        TurbolinksView turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visitLocationWithAction(location,action);
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

    public static void startActivity(BaseActivity menuActivity) {
        Intent intent = new Intent(menuActivity,TurboLinkViewActivity.class);
        menuActivity.startActivity(intent);
    }
}
