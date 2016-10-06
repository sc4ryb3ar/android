package com.bitlove.fetlife.view.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.bitlove.fetlife.R;

public class ResourceListActivity extends ResourceActivity implements MenuActivityComponent.MenuActivityCallBack {

    protected FloatingActionButton floatingActionButton;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager recyclerLayoutManager;
    protected View inputLayout;
    protected View inputIcon;
    protected EditText textInput;

    private MenuActivityComponent menuActivityComponent;

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActivityContentView();
        menuActivityComponent = initMenuActivityComponent();
        menuActivityComponent.onActivityCreate(this);

        //TODO: consider removing this
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

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
        recyclerView.setLayoutManager(recyclerLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    protected void setActivityContentView() {
        setContentView(R.layout.activity_resource);
    }

    protected MenuActivityComponent initMenuActivityComponent() {
        return new MenuActivityComponent();
    }

    @Override
    protected void onResourceStart() {
    }

    @Override
    public boolean finishAtMenuNavigation() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (menuActivityComponent == null || !menuActivityComponent.onActivityBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menuActivityComponent != null) {
            return menuActivityComponent.onActivityCreateOptionsMenu(menu);
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (menuActivityComponent != null) {
            return menuActivityComponent.onActivityOptionsItemSelected(item);
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (menuActivityComponent == null || !menuActivityComponent.onActivityKeyDown(keyCode,e)) {
            return super.onKeyDown(keyCode, e);
        } else {
            return true;
        }
    }

}
