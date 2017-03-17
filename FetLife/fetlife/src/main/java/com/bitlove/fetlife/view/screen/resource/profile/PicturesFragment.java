package com.bitlove.fetlife.view.screen.resource.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Picture;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.UrlUtil;
import com.bitlove.fetlife.view.adapter.PicturesRecyclerAdapter;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.BaseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PicturesFragment extends BaseFragment implements PicturesRecyclerAdapter.OnPictureClickListener {

    private static final String ARG_MEMBER_ID = "ARG_MEMBER_ID";
    private static final int PICTURE_GRID_COLUMN_COUNT = 3;
    private RecyclerView recyclerView;

    public static PicturesFragment newInstance(String memberId) {
        //TODO(profile): make it work with current user too (save user as member and keep only id in other table)
        PicturesFragment picturesFragment = new PicturesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MEMBER_ID, memberId);
        picturesFragment.setArguments(args);
        return picturesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        GridLayoutManager recyclerLayoutManager = new GridLayoutManager(getFetLifeApplication(), PICTURE_GRID_COLUMN_COUNT);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        PicturesRecyclerAdapter adapter = new PicturesRecyclerAdapter(getArguments().getString(ARG_MEMBER_ID),this,getFetLifeApplication());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServiceCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent.getServiceCallAction().equals(FetLifeApiIntentService.ACTION_APICALL_MEMBER_PICTURES)) {
            refresh();
        }
    }

    public void refresh() {
        if (recyclerView != null) {
            PicturesRecyclerAdapter recyclerViewAdapter = (PicturesRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }


    private FetLifeApplication getFetLifeApplication() {
        return (FetLifeApplication) getActivity().getApplication();
    }

    @Override
    public void onVisitItem(Picture picture, String url) {
        UrlUtil.openUrl(getActivity(),url);
    }

    @Override
    public void onMemberClick(Member member) {
        ProfileActivity.startActivity((BaseActivity) getActivity(), member.getId());
    }
}
