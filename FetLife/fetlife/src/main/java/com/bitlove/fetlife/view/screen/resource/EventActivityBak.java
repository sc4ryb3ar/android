package com.bitlove.fetlife.view.screen.resource;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.util.DateUtil;
import com.bitlove.fetlife.view.screen.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.UUID;

public class EventActivityBak extends ResourceActivity {

    private static final String EXTRA_EVENT_ID = "EXTRA_EVENT_ID";
    private static final String DATE_INTERVAL_SEPARATOR = " - ";

    private String eventId;

    public static void startActivity(BaseActivity baseActivity, String eventId) {
        Intent intent = new Intent(baseActivity,EventActivityBak.class);
        intent.putExtra(EXTRA_EVENT_ID,eventId);
        baseActivity.startActivity(intent);
    }

    private Event event;

    @Override
    protected void onResourceCreate(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventId = savedInstanceState != null ? savedInstanceState.getString(EXTRA_EVENT_ID) : getIntent().getStringExtra(EXTRA_EVENT_ID);

        saveTempTestData();

        loadEvent();

        ActionBar supportActionBar = getSupportActionBar();
        //supportActionBar.setTitle("");
        //supportActionBar.setTitle(event.getName());
        setTitle(event.getName());
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);

        setText(R.id.text_event_tagline,event.getTagline());
        setText(R.id.text_event_value_location,event.getLocation());
        setText(R.id.text_event_value_address,event.getAddress());
        String startDateTime = event.getStartDateTime();
        startDateTime = (!TextUtils.isEmpty(startDateTime) ? SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(startDateTime)) : "");
        String endDateTime = event.getEndDateTime();
        endDateTime = (!TextUtils.isEmpty(endDateTime) ? SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(endDateTime)) : "");
        setText(R.id.text_event_value_date,startDateTime + DATE_INTERVAL_SEPARATOR + endDateTime);
        setText(R.id.text_event_value_dresscode,event.getDressCode());
        setText(R.id.text_event_value_cost,event.getCost());
        setText(R.id.text_event_description,event.getDescription());
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");
        getSupportActionBar().setTitle("");
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        TextView headerTitle = (TextView) findViewById(R.id.event_title);
        toolbarTitle.setText(title);
        headerTitle.setText(title);
    }

    private void loadEvent() {
        event = Event.loadEvent(eventId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(EXTRA_EVENT_ID,eventId);
        super.onSaveInstanceState(outState);
    }

    private void saveTempTestData() {
        if (TextUtils.isEmpty(eventId)) {
            eventId = UUID.randomUUID().toString();
            Event event = new Event();
            event.setId(eventId);
            event.setName("Luxuria Party");
            event.setTagline("Fetish-BDSM party");
            event.setLocation("Bakelit Multi Art Center");
            event.setAddress("Budapest, Soroksári. u 69.");
            event.setDressCode("Fetish");
            event.setCost("22-27 EUR");
            event.setDescription(getString(R.string.temp_large_text));
            event.save();
        }
    }

    @Override
    protected void onCreateActivityComponents() {
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_event_new_bak);
    }

    @Override
    protected void onResourceStart() {

    }

    private void setText(int id, String text) {
        TextView textView = (TextView) findViewById(id);
        textView.setText(text);
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

}
