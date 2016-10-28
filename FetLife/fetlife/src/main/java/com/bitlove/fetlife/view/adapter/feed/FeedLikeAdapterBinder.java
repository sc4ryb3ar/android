
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
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.Event;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Picture;
import com.bitlove.fetlife.model.pojos.SecondaryTarget;
import com.bitlove.fetlife.model.pojos.Story;
import com.bitlove.fetlife.model.pojos.Target;
import com.bitlove.fetlife.util.DateUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FeedLikeAdapterBinder {

    private final FeedRecyclerAdapter feedRecyclerAdapter;

    private SparseArray<Boolean> expandHistory = new SparseArray<>();

    public FeedLikeAdapterBinder(FeedRecyclerAdapter feedRecyclerAdapter) {
        this.feedRecyclerAdapter = feedRecyclerAdapter;
    }

    public void bindLikeStory(FetLifeApplication fetLifeApplication, final FeedViewHolder feedViewHolder, final Story story, final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {

        Context context = feedViewHolder.avatarImage.getContext();

        final int position = feedViewHolder.getAdapterPosition();

        final List<Event> events = story.getEvents();
        if (events.isEmpty()) {
            return;
        }

        final Member liker;
        BaseAdapter gridAdapter = null, listAdapter = null;
        String createdAt = null, name = null, meta = null, title = null;
        Integer expandableResourceId = null;
        try {
            Target target = events.get(0).getTarget();
            SecondaryTarget secondaryTarget = events.get(0).getSecondaryTarget();

            liker = target.getLove().getMember();

            Picture picture = secondaryTarget.getPicture();
            if (picture != null) {
                createdAt = picture.getCreatedAt();
                name = liker.getNickname();
                meta = liker.getMetaInfo();
                title = events.size() == 1 ? context.getString(R.string.feed_title_like_picture) : context.getString(R.string.feed_title_like_pictures, events.size());
                gridAdapter = new PictureGridAdapter(events, onItemClickListener);
                expandableResourceId = R.id.feeditem_grid_expandable;
            }

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

        String avatarLink = liker.getAvatarLink();
        if (avatarLink == null) {
            feedViewHolder.avatarImage.setImageURI((String)null);
        } else {
            Uri avatarUri = Uri.parse(avatarLink);
            feedViewHolder.avatarImage.setImageURI(avatarUri);
        }
        feedViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onMemberClick(liker);
            }
        });

        if (gridAdapter != null) {
            GridView gridLayout = feedViewHolder.gridExpandArea;
            gridLayout.setAdapter(gridAdapter);
            boolean expandByPreference = fetLifeApplication.getUserSessionManager().getActiveUserPreferences().getBoolean(context.getString(R.string.settings_key_feed_auto_expand_like),false);
            boolean expanded = expandHistory.get(position,expandByPreference);
            gridLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
            feedViewHolder.separatorView.setVisibility(expanded ? View.VISIBLE : View.GONE);
            feedViewHolder.listExpandArea.setVisibility(View.GONE);
        }
    }

    static class PictureGridAdapter extends BaseAdapter {
        private final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener;

        private List<Picture> pictures = new ArrayList<>();
        private ArrayList<String> gridLinks = new ArrayList<>();
        private ArrayList<String> displayLinks = new ArrayList<>();

        PictureGridAdapter(List<Event> events, FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
            for (Event event : events) {
                Picture picture = event.getSecondaryTarget().getPicture();
                pictures.add(picture);
                gridLinks.add(picture.getVariants().getMedium().getUrl());
                displayLinks.add(picture.getVariants().getHuge().getUrl());
            }
        }

        @Override
        public int getCount() {
            return pictures.size();
        }

        @Override
        public Picture getItem(int position) {
            return pictures.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            Uri pictureUri;

            try {
                pictureUri = Uri.parse(gridLinks.get(position));
            } catch (NullPointerException npe) {
                return new LinearLayout(parent.getContext());
            }
            if (pictureUri == null) {
                return new LinearLayout(parent.getContext());
            }

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) inflater.inflate(R.layout.listitem_feed_griditem, parent, false);
            simpleDraweeView.setImageURI(pictureUri);
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(v.getContext());
                    final View overlay = inflater.inflate(R.layout.feed_image_swipe_overlay, null);
                    TextView imageDescription = (TextView) overlay.findViewById(R.id.feedImageOverlayDescription);
                    TextView imageMeta = (TextView) overlay.findViewById(R.id.feedImageOverlayMeta);
                    TextView imageName = (TextView) overlay.findViewById(R.id.feedImageOverlayName);
                    final Picture picture = pictures.get(position);
                    imageName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onItemClickListener.onMemberClick(picture.getMember());
                        }
                    });
                    imageDescription.setText(picture.getBody());
                    imageMeta.setText(picture.getMember().getMetaInfo());
                    imageName.setText(picture.getMember().getNickname());
                    new ImageViewer.Builder(v.getContext(), displayLinks).setStartPosition(position).setOverlayView(overlay).setImageChangeListener(new ImageViewer.OnImageChangeListener() {
                        @Override
                        public void onImageChange(int position) {
                            TextView imageDescription = (TextView) overlay.findViewById(R.id.feedImageOverlayDescription);
                            TextView imageMeta = (TextView) overlay.findViewById(R.id.feedImageOverlayMeta);
                            TextView imageName = (TextView) overlay.findViewById(R.id.feedImageOverlayName);
                            final Picture picture = pictures.get(position);
                            imageName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onItemClickListener.onMemberClick(picture.getMember());
                                }
                            });
                            imageDescription.setText(picture.getBody());
                            imageMeta.setText(picture.getMember().getMetaInfo());
                            imageName.setText(picture.getMember().getNickname());
                        }
                    }).show();
                }
            });

            return simpleDraweeView;
        }

    }

}
