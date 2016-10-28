
package com.bitlove.fetlife.view.adapter.feed;

import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Event;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Relation;
import com.bitlove.fetlife.model.pojos.Story;
import com.bitlove.fetlife.model.pojos.Target;
import com.bitlove.fetlife.util.DateUtil;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.SimpleDateFormat;
import java.util.List;

public class FeedFriendsAdapterBinder {

    private SparseArray<Boolean> expandHistory = new SparseArray<>();

    private final FeedRecyclerAdapter feedRecyclerAdapter;

    public FeedFriendsAdapterBinder(FeedRecyclerAdapter feedRecyclerAdapter) {
        this.feedRecyclerAdapter = feedRecyclerAdapter;
    }

    public void bindRelationStory(final FeedViewHolder feedViewHolder, final Story story, final ResourceListRecyclerAdapter.OnResourceClickListener<Story> onItemClickListener) {

        Context context = feedViewHolder.avatarImage.getContext();

        final int position = feedViewHolder.getAdapterPosition();

        final List<Event> events = story.getEvents();
        if (events.isEmpty()) {
            return;
        }

        Member followed;
        BaseAdapter gridAdapter = null, listAdapter = null;
        String createdAt = null, meta = null, name = null, title = null;
        Integer expandableResourceId = null;
        try {
            Target target = events.get(0).getTarget();
            Relation relation = target.getRelation();

            followed = relation.getMember();
            createdAt = relation.getCreatedAt();
            name = followed.getNickname();
            meta = followed.getMetaInfo();
            title = events.size() == 1 ? context.getString(R.string.feed_title_new_relation) : context.getString(R.string.feed_title_new_relations, events.size());

            gridAdapter = new PictureGridAdapter(events);
            expandableResourceId = R.id.feeditem_grid_expandable;

        } catch (NullPointerException npe) {
            return;
        }


        if (title != null && name != null && meta != null && createdAt != null) {
            feedViewHolder.feedText.setText(title);
            feedViewHolder.nameText.setText(name);
            feedViewHolder.metaText.setText(meta);
            feedViewHolder.timeText.setText(SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(createdAt)));
        } else {
            feedViewHolder.feedText.setText(R.string.feed_title_relation_unknown);
            feedViewHolder.metaText.setText(null);
            feedViewHolder.nameText.setText(null);
            feedViewHolder.timeText.setText(null);
            feedViewHolder.gridExpandArea.setAdapter(null);
            feedViewHolder.listExpandArea.removeAllViews();
        }

        final Integer resourceRef = expandableResourceId;
        feedViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resourceRef != null) {
                    View view2Expand = v.findViewById(resourceRef);
                    boolean visible = view2Expand.getVisibility() == View.VISIBLE;
                    view2Expand.setVisibility(visible ? View.GONE : View.VISIBLE);
                    v.findViewById(R.id.feeditem_separator).setVisibility(visible ? View.GONE : View.VISIBLE);
                    expandHistory.put(position,!visible);
                }
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(story);
                }
            }
        });

        feedViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onAvatarClick(story);
                }
            }
        });

        feedViewHolder.avatarImage.setBackgroundResource(R.drawable.dummy_avatar);

        String avatarLink = followed.getAvatarLink();
        if (avatarLink == null) {
            feedViewHolder.avatarImage.setImageURI((String)null);
        } else {
            Uri avatarUri = Uri.parse(avatarLink);
            feedViewHolder.avatarImage.setImageURI(avatarUri);
        }
        feedViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onAvatarClick(story);
            }
        });


        if (gridAdapter != null) {
            GridView gridLayout = feedViewHolder.gridExpandArea;
            gridLayout.setAdapter(gridAdapter);
            boolean expandByPreference = FetLifeApplication.getInstance().getUserSessionManager().getActiveUserPreferences().getBoolean(context.getString(R.string.settings_key_feed_auto_expand_relation),false);
            boolean expanded = expandHistory.get(position,expandByPreference);
            gridLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            feedViewHolder.separatorView.setVisibility(expanded ? View.VISIBLE : View.GONE);
            feedViewHolder.listExpandArea.setVisibility(View.GONE);
        }
    }

    static class PictureGridAdapter extends BaseAdapter {
        private List<Event> events;

        PictureGridAdapter(List<Event> events) {
            this.events = events;
        }

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Event getItem(int position) {
            return events.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Uri pictureUri;

            try {
                String avatarLink = getItem(position).getTarget().getRelation().getTargetMember().getAvatarLink();
                pictureUri = Uri.parse(avatarLink);
            } catch (NullPointerException npe) {
                return new LinearLayout(parent.getContext());
            }
            if (pictureUri == null) {
                return new LinearLayout(parent.getContext());
            }

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) inflater.inflate(R.layout.listitem_feed_griditem, parent, false);
            simpleDraweeView.setImageURI(pictureUri);

            return simpleDraweeView;

        }
    }

}
