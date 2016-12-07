package com.bitlove.fetlife.view.activity.web;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;

public class TouActivity extends AppCompatActivity implements TurbolinksAdapter {

    private TurbolinksView turbolinksView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_tou);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);
        if (turbolinksView == null) {
            return;
        }

        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit("https://fetlife.com/legalese/tou");
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
        TurbolinksSession.getDefault(this).runJavascriptRaw("(function(){document.body.innerHTML = document.body.innerHTML.replace('<a class=\"o-nav__heart o-nav__icon\"', '<!--<a class=\"o-nav__heart o-nav__icon\"')})()");
        TurbolinksSession.getDefault(this).runJavascriptRaw("(function(){document.body.innerHTML = document.body.innerHTML.replace('<div class=\"o-subnav\">', '--></div></div><div class=\"o-subnav\">')})()");
    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visitLocationWithAction(location,action);
    }

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, TouActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

}
