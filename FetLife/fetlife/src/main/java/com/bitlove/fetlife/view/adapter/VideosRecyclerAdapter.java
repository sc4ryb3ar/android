package com.bitlove.fetlife.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Video;
import com.bitlove.fetlife.model.pojos.VideoReference;
import com.bitlove.fetlife.model.pojos.VideoReference_Table;
import com.bitlove.fetlife.model.pojos.Video_Table;
import com.bitlove.fetlife.util.UrlUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

public class VideosRecyclerAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private final String memberId;
    private List<Video> itemList;

    public VideosRecyclerAdapter(String memberId) {
        this.memberId = memberId;
        loadItems();
    }

    public void refresh() {
        loadItems();
        //TODO: think of possibility of update only specific items instead of the whole list
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            List<VideoReference> videoReferences = new Select().from(VideoReference.class).where(VideoReference_Table.userId.is(memberId)).orderBy(OrderBy.fromProperty(VideoReference_Table.date).descending()).queryList();
            List<String> videoIds = new ArrayList<>();
            for (VideoReference videoReference : videoReferences) {
                videoIds.add(videoReference.getId());
            }
            //TODO(profile):add proper ordering
            itemList = new Select().from(Video.class).where(Video_Table.id.in(videoIds)).orderBy(OrderBy.fromProperty(Video_Table.date).descending()).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_video, parent, false);
        return new VideoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, final int position) {
        final Video video = itemList.get(position);
        holder.imageView.setImageURI(video.getThumbUrl());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UrlUtil.openUrl(view.getContext(),video.getUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}

class VideoViewHolder extends RecyclerView.ViewHolder {

    SimpleDraweeView imageView;

    public VideoViewHolder(View itemView) {
        super(itemView);
        imageView = (SimpleDraweeView) itemView.findViewById(R.id.profile_video);
    }
}

