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
        orientationTextView.setText(getOrientationText(orientation));

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
            lookingForTextView.setText(getLookingForDisplayString(lookingForText));
        } else {
            lookingForRowView.setVisibility(View.GONE);
            lookingForTextView.setText("");
        }
    }

    private String getOrientationText(String orientation) {
        if (orientation == null) {
            return null;
        }
        switch (orientation) {
            case "Straight":
                return getString(R.string.text_profile_orientation_straight);
            case "Heteroflexible":
                return getString(R.string.text_profile_orientation_heteroflexible);
            case "Bisexual":
                return getString(R.string.text_profile_orientation_bisexual);
            case "Homoflexible":
                return getString(R.string.text_profile_orientation_homoflexible);
            case "Gay":
                return getString(R.string.text_profile_orientation_gay);
            case "Lesbian":
                return getString(R.string.text_profile_orientation_lesbian);
            case "Queer":
                return getString(R.string.text_profile_orientation_queer);
            case "Pansexual":
                return getString(R.string.text_profile_orientation_pansexual);
            case "Fluctuating/Evolving":
                return getString(R.string.text_profile_orientation_evolving);
            case "Asexual":
                return getString(R.string.text_profile_orientation_asexual);
            case "Unsure":
                return getString(R.string.text_profile_orientation_unsure);
            case "Not Applicable":
                return getString(R.string.text_profile_orientation_na);
            default:
                return orientation;
        }
    }

    private String getLookingForDisplayString(String lookingForText) {
        if (lookingForText == null) {
            return null;
        }
        switch (lookingForText) {
            case "lifetime_relationship":
                return getString(R.string.text_profile_lookingfor_ltr);
            case "relationship":
                return getString(R.string.text_profile_lookingfor_relationship);
            case "teacher":
                return getString(R.string.text_profile_lookingfor_teacher);
            case "someone_to_play_with":
                return getString(R.string.text_profile_lookingfor_playpartner);
            case "princess_by_day_slut_by_night":
                return getString(R.string.text_profile_lookingfor_princessslut);
            case "friendship":
                return getString(R.string.text_profile_lookingfor_friendship);
            case "slave":
                return getString(R.string.text_profile_lookingfor_slave);
            case "sub":
                return getString(R.string.text_profile_lookingfor_sub);
            case "master":
                return getString(R.string.text_profile_lookingfor_master);
            case "mistress":
                return getString(R.string.text_profile_lookingfor_mistress);
            case "fetnights":
                return getString(R.string.text_profile_lookingfor_events);
            default:
                return lookingForText;
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
