package com.bitlove.fetlife.view.screen.resource.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.screen.BaseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class BasicInfoFragment extends ProfileFragment {

    private static final String ARG_MEMBER_ID = "ARG_MEMBER_ID";
    private View locationRowView, relationshipRowView, orientationRowView, lookingForRowView;
    private TextView locationTextView, relationshipTextView, orientationTextView, lookingForTextView;

    public static BasicInfoFragment newInstance(String memberId) {
        //TODO(profile): make it work with current user too (mergeSave user as member and keep only id in other table)
        BasicInfoFragment aboutFragment = new BasicInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_ID, memberId);
        aboutFragment.setArguments(args);
        return aboutFragment;
    }

    private void loadAndSetAbout() {
        Member member = Member.loadMember(getArguments().getString(ARG_MEMBER_ID));
        if (member == null) {
            return;
        }
        //TODO(profile) use localized values

        String country = member.getCountry();
        String city = member.getCity();
        String administrativeArea = member.getAdministrativeArea();

        String location = null;
        if (country != null) {
            if (city != null || administrativeArea != null) {
                location = getString(R.string.text_profile_location,member.getCountry(),member.getCity() != null ? member.getCity() : member.getAdministrativeArea());
            } else {
                location = country;
            }
        } else if (city != null || administrativeArea != null) {
            location = city != null ? city : administrativeArea;
        }

        locationRowView.setVisibility(location != null ? View.VISIBLE : View.GONE);
        locationTextView.setText(location);

        String orientation = member.getSexualOrientation();

        orientationRowView.setVisibility(orientation != null ? View.VISIBLE : View.GONE);
        orientationTextView.setText(orientation);

        List<String> lookingFors = member.getLookingFor();
        if (lookingFors != null && !lookingFors.isEmpty()) {
            lookingForRowView.setVisibility(View.VISIBLE);
            String lookingForText = "";
            for (String lookingFor : lookingFors) {
                if (lookingFor == null || lookingFor.isEmpty()) {
                    continue;
                }
                lookingForText += lookingFor + "\n";
            }
            lookingForTextView.setText(lookingForText);
        } else {
            lookingForRowView.setVisibility(View.GONE);
            lookingForTextView.setText("");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_basicinfo, container, false);
        locationRowView = view.findViewById(R.id.text_profile_row_location);
        relationshipRowView = view.findViewById(R.id.text_profile_row_relationship);
        orientationRowView = view.findViewById(R.id.text_profile_row_orientation);
        lookingForRowView = view.findViewById(R.id.text_profile_row_lookingfor);
        locationTextView = (TextView) view.findViewById(R.id.text_profile_value_location);
        relationshipTextView = (TextView) view.findViewById(R.id.text_profile_value_relationship);
        orientationTextView = (TextView) view.findViewById(R.id.text_profile_value_orientation);
        lookingForTextView = (TextView) view.findViewById(R.id.text_profile_value_lookingfor);
        loadAndSetAbout();
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER;
    }

    @Override
    public void refresh() {
        loadAndSetAbout();
    }

}
