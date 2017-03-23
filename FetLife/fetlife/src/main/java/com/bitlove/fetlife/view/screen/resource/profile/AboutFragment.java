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

public class AboutFragment extends BaseFragment implements ProfileFragment {

    private static final String ARG_MEMBER_ID = "ARG_MEMBER_ID";
    private TextView aboutTextView;

    public static AboutFragment newInstance(String memberId) {
        //TODO(profile): make it work with current user too (mergeSave user as member and keep only id in other table)
        AboutFragment aboutFragment = new AboutFragment();
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
        aboutTextView.setText(member.getAbout());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_about, container, false);
        aboutTextView = (TextView) view.findViewById(R.id.text_profile_about);
        loadAndSetAbout();
        return view;
    }

    @Override
    public void refresh() {
        loadAndSetAbout();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_MEMBER)) {
            refresh();
        }
    }
}
