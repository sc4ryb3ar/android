package com.bitlove.fetlife.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.User;
import com.bitlove.fetlife.view.dialog.MediaUploadSelectionDialog;

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

        //TODO: think of moving content stuff out of this class/method
        setContentView(R.layout.activity_resource);

        menuActivityComponent = new MenuActivityComponent();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        menuActivityComponent.onActivityCreate(this);

        inputLayout = findViewById(R.id.text_input_layout);
        inputIcon = findViewById(R.id.text_send_icon);
        textInput = (EditText) findViewById(R.id.text_input);
        //textInput.setSingleLine(false);
        //textInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
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

    @Override
    protected void onResourceStart() {
    }

    @Override
    public boolean finishAtMenuNavigation() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!menuActivityComponent.onActivityBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return menuActivityComponent.onActivityCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return menuActivityComponent.onActivityOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {
        if (!menuActivityComponent.onActivityKeyDown(keyCode,e)) {
            return super.onKeyDown(keyCode, e);
        } else {
            return true;
        }
    }

}
