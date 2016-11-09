
package com.bitlove.fetlife.view.adapter.feed;

import android.content.Context;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Event;
import com.bitlove.fetlife.model.pojos.FeedEvent;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Rsvp;
import com.bitlove.fetlife.model.pojos.Story;
import com.bitlove.fetlife.model.pojos.Target;
import com.bitlove.fetlife.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FeedRsvpAdapterBinder {

    private enum RsvpStatus {
        YES,
        MAYBE
    }

    private final FeedRecyclerAdapter feedRecyclerAdapter;

    private SparseArray<Boolean> expandHistory = new SparseArray<>();

    public FeedRsvpAdapterBinder(FeedRecyclerAdapter feedRecyclerAdapter) {
        this.feedRecyclerAdapter = feedRecyclerAdapter;
    }

    public void bindRsvpStory(FetLifeApplication fetLifeApplication, final FeedViewHolder feedViewHolder, final Story story, final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {

        Context context = feedViewHolder.avatarImage.getContext();

        final int position = feedViewHolder.getAdapterPosition();

        final List<FeedEvent> events = story.getEvents();
        if (events.isEmpty()) {
            return;
        }

        final Member member;
        String createdAt, name, meta, title;
        Integer expandableResourceId;
        BaseAdapter listAdapter = null;
        try {
            Target target = events.get(0).getTarget();

            Rsvp rsvp = target.getRsvp();
            member = rsvp.getMember();

            createdAt = rsvp.getCreatedAt();
            name = member.getNickname();
            meta = member.getMetaInfo();

            RsvpStatus rsvpStatus;
            try {
                rsvpStatus  = RsvpStatus.valueOf(rsvp.getStatus().toUpperCase());
            } catch (IllegalArgumentException iae) {
                rsvpStatus = null;
            }

            switch (rsvpStatus) {
                case YES:
                    title = events.size() == 1 ? context.getString(R.string.feed_title_rsvp_yes) : context.getString(R.string.feed_title_rsvps_yes, events.size());
                    break;
                //TODO (feed): check other status value
                default:
                    title = events.size() == 1 ? context.getString(R.string.feed_title_rsvp_maybe) : context.getString(R.string.feed_title_rsvps_maybe, events.size());
                    break;
            }

            listAdapter = new RsvpListAdapter(events, onItemClickListener);

            expandableResourceId = R.id.feeditem_list_expandable;

        } catch (NullPointerException npe) {
            return;
        }


        if (title != null && name != null && meta != null && createdAt != null) {
            feedViewHolder.feedText.setText(title);
            feedViewHolder.nameText.setText(name);
            feedViewHolder.metaText.setText(meta);
            feedViewHolder.timeText.setText(SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(createdAt)));
        } else {
            feedViewHolder.feedText.setText(R.string.feed_title_like_unknown);
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
            }
        });

        feedViewHolder.avatarImage.setBackgroundResource(R.drawable.dummy_avatar);

        String avatarLink = member.getAvatarLink();
        if (avatarLink == null) {
            feedViewHolder.avatarImage.setImageURI((String)null);
        } else {
            Uri avatarUri = Uri.parse(avatarLink);
            feedViewHolder.avatarImage.setImageURI(avatarUri);
        }
        feedViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onMemberClick(member);
            }
        });

        if (listAdapter != null) {
            LinearLayout linearLayout = feedViewHolder.listExpandArea;
            addItems(linearLayout, listAdapter, onItemClickListener);
            boolean expandByPreference = fetLifeApplication.getUserSessionManager().getActiveUserPreferences().getBoolean(context.getString(R.string.settings_key_feed_auto_expand_like),false);
            boolean expanded = expandHistory.get(position,expandByPreference);
            linearLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            feedViewHolder.separatorView.setVisibility(expanded ? View.VISIBLE : View.GONE);
            feedViewHolder.gridExpandArea.setVisibility(View.GONE);
        }
    }

    private void addItems(LinearLayout linearLayout, final BaseAdapter listAdapter, final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {
        linearLayout.removeAllViews();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View itemView = listAdapter.getView(i, null, linearLayout);
            final int position = i;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onEventClick((Event) listAdapter.getItem(position));
                }
            });
            linearLayout.addView(itemView);
        }
    }

    static class RsvpListAdapter extends BaseAdapter {
        private final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener;

        private List<Rsvp> rsvps = new ArrayList<>();

        RsvpListAdapter(List<FeedEvent> events, FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            for (FeedEvent event : events) {
                Rsvp rsvp = event.getTarget().getRsvp();
                rsvps.add(rsvp);
            }
        }

        @Override
        public int getCount() {
            return rsvps.size();
        }

        @Override
        public Event getItem(int position) {
            return rsvps.get(position).getEvent();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View resultView = inflater.inflate(R.layout.listitem_feed_innerlistitem, parent, false);
            Rsvp rsvp = rsvps.get(position);

            TextView itemHeader = (TextView) resultView.findViewById(R.id.feed_innerlist_header);
            itemHeader.setText(rsvp.getEvent().getName());
            TextView itemText = (TextView) resultView.findViewById(R.id.feed_innerlist_upper);
            itemText.setText(rsvp.getEvent().getLocation());
            TextView timeText = (TextView) resultView.findViewById(R.id.feed_innerlist_right);
            timeText.setText(SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(rsvp.getEvent().getStartDateTime())));

            return resultView;
        }

    }

}
