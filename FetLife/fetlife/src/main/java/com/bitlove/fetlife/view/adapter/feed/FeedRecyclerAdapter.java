package com.bitlove.fetlife.view.adapter.feed;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Event;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Story;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.SwipeableViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class FeedRecyclerAdapter extends ResourceListRecyclerAdapter<Story, FeedViewHolder> {

    public interface OnFeedItemClickListener {
        void onMemberClick(Member member);
        void onEventClick(Event event);
    }

    private final FetLifeApplication fetLifeApplication;
    private final OnFeedItemClickListener onFeedItemClickListener;

    private List<Story> itemList;
    private SparseArray<Boolean> expandedMap = new SparseArray<>();

    FeedLikeAdapterBinder feedLikeAdapterBinder = new FeedLikeAdapterBinder(this);
    FeedRelationshipAdapterBinder feedRelationshipAdapterBinder = new FeedRelationshipAdapterBinder(this);
    FeedNotSupportedAdapterBinder feedNotSupportedAdapterBinder = new FeedNotSupportedAdapterBinder(this);
    FeedRsvpAdapterBinder feedRsvpAdapterBinder = new FeedRsvpAdapterBinder(this);

    public FeedRecyclerAdapter(FetLifeApplication fetLifeApplication, OnFeedItemClickListener onFeedItemClickListener) {
        this.fetLifeApplication = fetLifeApplication;
        this.onFeedItemClickListener = onFeedItemClickListener;
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

    public Story getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_feed, parent, false);
        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedViewHolder feedViewHolder, int position) {

        Story story = getItem(position);

        switch (story.getName()) {
            case "like_created":
                feedLikeAdapterBinder.bindLikeStory(fetLifeApplication, feedViewHolder, story, onFeedItemClickListener);
                break;
            case "follow_created":
                feedRelationshipAdapterBinder.bindFollowStory(fetLifeApplication, feedViewHolder, story, onFeedItemClickListener);
                break;
            case "friend_created":
                feedRelationshipAdapterBinder.bindFriendStory(fetLifeApplication, feedViewHolder, story, onFeedItemClickListener);
                break;
            case "rsvp_created":
                feedRsvpAdapterBinder.bindRsvpStory(fetLifeApplication, feedViewHolder, story, onFeedItemClickListener);
                break;
            default:
                feedNotSupportedAdapterBinder.bindNotSupportedStory(fetLifeApplication, feedViewHolder,story, onFeedItemClickListener);
                break;
        }
    }

    @Override
    protected void onItemRemove(FeedViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }
}

class FeedViewHolder extends SwipeableViewHolder {

    GridView gridExpandArea;
    LinearLayout listExpandArea;
    SimpleDraweeView avatarImage;
    TextView nameText, metaText, feedText, timeText;
    View separatorView;

    public FeedViewHolder(View itemView) {
        super(itemView);

        feedText = (TextView) itemView.findViewById(R.id.feeditem_text);
        nameText = (TextView) itemView.findViewById(R.id.feeditem_name);
        metaText = (TextView) itemView.findViewById(R.id.feeditem_meta);
        timeText = (TextView) itemView.findViewById(R.id.feeditem_time);
        avatarImage = (SimpleDraweeView) itemView.findViewById(R.id.feeditem_icon);
        separatorView = itemView.findViewById(R.id.feeditem_separator);

        gridExpandArea = (GridView) itemView.findViewById(R.id.feeditem_grid_expandable);
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

