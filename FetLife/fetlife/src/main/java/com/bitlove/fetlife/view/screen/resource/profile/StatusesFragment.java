package com.bitlove.fetlife.view.screen.resource.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.adapter.StatusesRecyclerAdapter;
import com.bitlove.fetlife.view.screen.resource.LoadFragment;

public class StatusesFragment extends LoadFragment {

    public static StatusesFragment newInstance(String memberId) {
        StatusesFragment statusesFragment = new StatusesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_REFERENCE_ID, memberId);
        statusesFragment.setArguments(args);
        return statusesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(getFetLifeApplication());
        recyclerView.setLayoutManager(recyclerLayoutManager);
        StatusesRecyclerAdapter adapter = new StatusesRecyclerAdapter(getArguments().getString(ARG_REFERENCE_ID));
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_MEMBER_STATUSES;
    }

    public void refreshUi() {
        if (recyclerView != null) {
            StatusesRecyclerAdapter recyclerViewAdapter = (StatusesRecyclerAdapter) recyclerView.getAdapter();
            recyclerViewAdapter.refresh();
        }
    }
}
