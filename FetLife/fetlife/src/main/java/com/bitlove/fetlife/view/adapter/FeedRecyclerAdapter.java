package com.bitlove.fetlife.view.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Event;
import com.bitlove.fetlife.model.pojos.FeedProcessor;
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

        Context context = feedViewHolder.avatarImage.getContext();

        final FeedStory feedStory = itemList.get(position);

        FeedProcessor feedProcesser = new FeedProcessor(feedStory);

        Member member = feedProcesser.getDisplayMember();
        Event templateEvent = feedProcesser.getTemplateEvent();
        int eventCount = feedProcesser.getEventCount();

        List<Event> events = feedProcesser.getEvents();
        FeedProcessor.FeedStoryType storyType = feedProcesser.getStoryType();


        feedViewHolder.headerText.setText(getTitle(storyType, member, eventCount, context));
        //feedViewHolder.timeText.setText(getTime(feedFeedStory));
//        feedViewHolder.timeText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(feedFeedStory.getEvents().get(0).getTarget().getCreatedAt())));

        feedViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onItemClick(feedStory);
                }
            }
        });

        feedViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getOnItemClickListener() != null) {
                    getOnItemClickListener().onAvatarClick(feedStory);
                }
            }
        });

        feedViewHolder.avatarImage.setBackgroundResource(R.drawable.dummy_avatar);

        String avatarLink;
        if (member == null || (avatarLink = member.getAvatarLink()) == null) {
            feedViewHolder.avatarImage.setImageURI((String)null);
        } else {
            Uri avatarUri = Uri.parse(avatarLink);
            feedViewHolder.avatarImage.setImageURI(avatarUri);
        }
    }

    private String getTitle(FeedProcessor.FeedStoryType storyType, Member member, int eventCount, Context context) {

        switch (storyType) {
            case FRIENDS:
                return context.getString(R.string.feed_title_friends, member.getNickname(), eventCount);
            case LOVED:
                return context.getString(R.string.feed_title_like, member.getNickname(), eventCount);
            default:
                if (member == null) {
                    return storyType.toString();
                }
                return member.getNickname() + " " + storyType.toString() + " " + eventCount;
        }
    }

    @Override
    protected void onItemRemove(FeedViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }
}

class FeedViewHolder extends SwipeableViewHolder {

    private final GridLayout gridExpandArea;
    private final LinearLayout listExpandArea;
    SimpleDraweeView avatarImage;
    TextView headerText, timeText;

    public FeedViewHolder(View itemView) {
        super(itemView);

        headerText = (TextView) itemView.findViewById(R.id.feeditem_header);
        timeText = (TextView) itemView.findViewById(R.id.feeditem_time);
        avatarImage = (SimpleDraweeView) itemView.findViewById(R.id.feeditem_icon);

        gridExpandArea = (GridLayout) itemView.findViewById(R.id.feeditem_grid_expandable);
        listExpandArea = (LinearLayout) itemView.findViewById(R.id.feeditem_list_expandable);
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

