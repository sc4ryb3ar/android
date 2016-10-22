package com.bitlove.fetlife.view.adapter;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Event;
import com.bitlove.fetlife.model.pojos.FeedStory;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Target;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FeedRecyclerAdapter extends ResourceListRecyclerAdapter<FeedStory, FeedViewHolder> {

    private final FetLifeApplication fetLifeApplication;
    private List<FeedStory> itemList;

    public FeedRecyclerAdapter(FetLifeApplication fetLifeApplication) {
        this.fetLifeApplication = fetLifeApplication;
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
        itemList = fetLifeApplication.getInMemoryStorage().getFeed();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public FeedStory getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_feed, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder feedViewHolder, int position) {

        final FeedStory feedFeedStory = itemList.get(position);

                feedViewHolder.headerText.setText(getTitle(feedFeedStory));
        feedViewHolder.timeText.setText(getTime(feedFeedStory));
//        feedViewHolder.timeText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(feedFeedStory.getEvents().get(0).getTarget().getCreatedAt())));

        feedViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onItemClick(feedFeedStory);
                }
            }
        });

        feedViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onAvatarClick(feedFeedStory);
                }
            }
        });

        String avatarUrl = feedFeedStory.getEvents().get(0).getTarget().getMember().getAvatarLink();
        Uri avatarUri = Uri.parse(avatarUrl);
        feedViewHolder.avatarImage.setImageURI(avatarUri);
    }

    private String getTitle(FeedStory feedFeedStory) {
        List<Event> events = feedFeedStory.getEvents();
        if (events.isEmpty()) {
            throw new IllegalArgumentException("A feed story must contain at least one event");
        }

        Member member = events.get(0).getTarget().getMember();

        switch (feedFeedStory.getName()) {
            default:
                return member.getNickname() + " " + feedFeedStory.getName() + events.size();
        }
    }

    @Override
    protected void onItemRemove(FeedViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }
}

class FeedViewHolder extends SwipeableViewHolder {

    private final GridLayout gridExpandArea;
    private final GridLayout listExpandArea;
    SimpleDraweeView avatarImage;
    TextView headerText, timeText;

    public FeedViewHolder(View itemView) {
        super(itemView);

        headerText = (TextView) itemView.findViewById(R.id.feeditem_header);
        timeText = (TextView) itemView.findViewById(R.id.feeditem_time);
        avatarImage = (SimpleDraweeView) itemView.findViewById(R.id.feeditem_icon);

        gridExpandArea = (GridLayout) itemView.findViewById(R.id.feeditem_grid_expandable);
        listExpandArea = (GridLayout) itemView.findViewById(R.id.feeditem_list_expandable);
    }

    @Override
    public View getSwipeableLayout() {
        return null;
    }

    @Override
    public View getSwipeRightBackground() {
        return null;
    }

    @Override
    public View getSwipeLeftBackground() {
        return null;
    }
}

