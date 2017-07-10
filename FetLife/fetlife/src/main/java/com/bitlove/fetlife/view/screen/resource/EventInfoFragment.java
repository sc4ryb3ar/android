package com.bitlove.fetlife.view.screen.resource;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.util.DateUtil;

import java.text.SimpleDateFormat;

public class EventInfoFragment extends LoadFragment {

    private static final String ARG_EVENT_ID = "ARG_EVENT_ID";
    private static final String DATE_INTERVAL_SEPARATOR = " - ";
    private TextView locationTextView,addressTextView,dateTextView,dresscodeTextView,costTextView,descriptionTextView;

    public static EventInfoFragment newInstance(String eventId) {
        EventInfoFragment aboutFragment = new EventInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        aboutFragment.setArguments(args);
        return aboutFragment;
    }

    private void loadAndSetDetails() {
        Event event = Event.loadEvent(getArguments().getString(ARG_EVENT_ID));
        if (event == null) {
            return;
        }
        locationTextView.setText(event.getLocation());
        addressTextView.setText(event.getAddress());
        String startDateTime = event.getStartDateTime();
        startDateTime = (!TextUtils.isEmpty(startDateTime) ? SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(startDateTime)) : "");
        String endDateTime = event.getEndDateTime();
        endDateTime = (!TextUtils.isEmpty(endDateTime) ? SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(endDateTime)) : "");
        dateTextView.setText(startDateTime + DATE_INTERVAL_SEPARATOR + endDateTime);
        dresscodeTextView.setText(event.getDressCode());
        costTextView.setText(event.getCost());
        descriptionTextView.setText(event.getDescription());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);
        locationTextView = (TextView) view.findViewById(R.id.text_event_value_location);
        addressTextView = (TextView) view.findViewById(R.id.text_event_value_address);
        dateTextView = (TextView) view.findViewById(R.id.text_event_value_date);
        dresscodeTextView = (TextView) view.findViewById(R.id.text_event_value_dresscode);
        costTextView = (TextView) view.findViewById(R.id.text_event_value_cost);
        descriptionTextView = (TextView) view.findViewById(R.id.text_event_description);
        loadAndSetDetails();
        return view;
    }

    @Override
    public String getApiCallAction() {
//        return FetLifeApiIntentService.ACTION_APICALL_EVENT;
        return null;
    }

    @Override
    public void refreshUi() {
        loadAndSetDetails();
    }

}
