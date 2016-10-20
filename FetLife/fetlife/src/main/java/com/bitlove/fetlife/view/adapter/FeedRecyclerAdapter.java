package com.bitlove.fetlife.view.adapter;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.model.pojos.Conversation_Table;
import com.bitlove.fetlife.model.pojos.Event;
import com.bitlove.fetlife.model.pojos.Feed;
import com.bitlove.fetlife.model.pojos.Story;
import com.bitlove.fetlife.model.resource.ImageLoader;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FeedRecyclerAdapter extends ResourceListRecyclerAdapter<Story, FeedViewHolder> {

    private final FetLifeApplication fetLifeApplication;
    private List<Story> itemList;

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

        final Story feedStory = itemList.get(position);
        List<Event> events = feedStory.getEvents();

        feedViewHolder.headerText.setText(feedStory.getName());
        feedViewHolder.timeText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(feedStory.getEvents().get(0).getTarget().getCreatedAt())));

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

        String avatarUrl = feedStory.getEvents().get(0).getTarget().getMember().getAvatarLink();
        Uri avatarUri = Uri.parse(avatarUrl);
        feedViewHolder.avatarImage.setImageURI(avatarUri);
    }

    @Override
    protected void onItemRemove(FeedViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }
}

class FeedViewHolder extends SwipeableViewHolder {

    SimpleDraweeView avatarImage;
    TextView headerText, timeText;

    public FeedViewHolder(View itemView) {
        super(itemView);

        headerText = (TextView) itemView.findViewById(R.id.feeditem_header);
        timeText = (TextView) itemView.findViewById(R.id.feeditem_time);
        avatarImage = (SimpleDraweeView) itemView.findViewById(R.id.feeditem_icon);

        
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

