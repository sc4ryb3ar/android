package com.bitlove.fetlife.view.screen.resource.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Member_Table;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.HtmlListTagHandler;
import com.bitlove.fetlife.view.screen.BaseFragment;
import com.raizlabs.android.dbflow.sql.language.BaseModelQueriable;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AboutFragment extends BaseFragment implements ProfileFragment {

    private static final String ARG_MEMBER_ID = "ARG_MEMBER_ID";

    public static AboutFragment newInstance(String memberId) {
        //TODO(profile): make it work with current user too (mergeSave user as member and keep only id in other table)
        AboutFragment aboutFragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_ID, memberId);
        aboutFragment.setArguments(args);
        return aboutFragment;
    }

    private String loadAbout(String memberId) {
        Member member = Member.loadMember(memberId);
        if (member == null) {
            return null;
        }
        return member.getAbout();
    }

    private void loadAndSetAbout(View view) {
        TextView aboutTextView = (TextView) view.findViewById(R.id.text_about);
        aboutTextView.setText(loadAbout(getArguments().getString(ARG_MEMBER_ID)));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_about, container, false);
//        view.findViewById(R.id.text_about).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int viewId = view.getId();
//                viewId++;
//            }
//        });
        loadAndSetAbout(view);
        return view;
    }

    @Override
    public void refresh() {
        loadAndSetAbout(getView());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_MEMBER)) {
            refresh();
        }
    }
}
