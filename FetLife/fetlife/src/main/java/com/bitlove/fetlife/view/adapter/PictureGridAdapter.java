package com.bitlove.fetlife.view.adapter;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.util.ViewUtil;
import com.bitlove.fetlife.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.view.adapter.feed.FeedRecyclerAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

public class PictureGridAdapter extends BaseAdapter {

    private static final int OVERLAY_HITREC_PADDING = 200;

    public static void setOverlayContent(View overlay, final Picture picture, final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                picture.setLastViewedAt(System.currentTimeMillis());
                picture.save();
            }
        });

        TextView imageDescription = (TextView) overlay.findViewById(R.id.feedImageOverlayDescription);
        TextView imageMeta = (TextView) overlay.findViewById(R.id.feedImageOverlayMeta);
        TextView imageName = (TextView) overlay.findViewById(R.id.feedImageOverlayName);

        final ImageView imageLove = (ImageView) overlay.findViewById(R.id.feedImageLove);
        ViewUtil.increaseTouchArea(imageLove,OVERLAY_HITREC_PADDING);

        boolean isLoved = picture.isLovedByMe();
        imageLove.setImageResource(isLoved ? R.drawable.ic_loved : R.drawable.ic_love);
        imageLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageLove = (ImageView) v;
                boolean isLoved = picture.isLovedByMe();
                boolean newIsLoved = !isLoved;
                imageLove.setImageResource(newIsLoved ? R.drawable.ic_loved : R.drawable.ic_love);
                Picture.startLoveCallWithObserver(FetLifeApplication.getInstance(), picture, newIsLoved);
                picture.setLovedByMe(newIsLoved);
            }
        });

        View imageVisit = overlay.findViewById(R.id.feedImageVisit);
        ViewUtil.increaseTouchArea(imageVisit,OVERLAY_HITREC_PADDING);
        imageVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onVisitItem(picture, picture.getUrl());
            }
        });

        ImageView imageShare = overlay.findViewById(R.id.feedImageShare);
        imageShare.setColorFilter(picture.isOnShareList() ? overlay.getContext().getResources().getColor(R.color.text_color_primary) : overlay.getContext().getResources().getColor(R.color.text_color_secondary));
        ViewUtil.increaseTouchArea(imageShare,OVERLAY_HITREC_PADDING);
        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onShareItem(picture, picture.getUrl());
                ((ImageView)v).setColorFilter(picture.isOnShareList() ? v.getContext().getResources().getColor(R.color.text_color_primary) : v.getContext().getResources().getColor(R.color.text_color_secondary));
            }
        });

        imageName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onMemberClick(picture.getMember());
            }
        });
        imageDescription.setText(Picture.getFormattedBody(picture.getBody()));
        imageMeta.setText(picture.getMember().getMetaInfo());
        imageName.setText(picture.getMember().getNickname());
    }

    private final FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener;
    private final FeedItemResourceHelper feedItemResourceHelper;
    private List<FeedEvent> events;

    private List<Picture> pictures = new ArrayList<>();
    private ArrayList<String> gridLinks = new ArrayList<>();
    private ArrayList<String> displayLinks = new ArrayList<>();

    public PictureGridAdapter(FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {
        this(null,onItemClickListener);
    }

    public PictureGridAdapter(FeedItemResourceHelper feedItemResourceHelper, FeedRecyclerAdapter.OnFeedItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.feedItemResourceHelper = feedItemResourceHelper;
    }

    public void setEvents(List<FeedEvent> events) {
        pictures.clear();
        gridLinks.clear();
        displayLinks.clear();
        this.events = events;
        for (FeedEvent event : events) {
            Picture picture = feedItemResourceHelper.getPicture(event);
            pictures.add(picture);
            gridLinks.add(picture != null ? picture.getVariants().getMediumUrl() : null);
            displayLinks.add(picture != null ? picture.getVariants().getHugeUrl() : null);
        }
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures.clear();
        gridLinks.clear();
        displayLinks.clear();
        for (Picture picture : pictures) {
            this.pictures.add(picture);
            gridLinks.add(picture != null ? picture.getThumbUrl() : null);
            displayLinks.add(picture != null ? picture.getDisplayUrl() : null);
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
        Uri pictureUri = gridLinks.get(position) != null ? Uri.parse(gridLinks.get(position)) : null;

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) inflater.inflate(R.layout.listitem_feed_griditem, parent, false);
        simpleDraweeView.setImageURI(pictureUri);
        if (pictureUri == null) {
            simpleDraweeView.getHierarchy().setPlaceholderImage(R.drawable.dummy_avatar);
        } else {
            simpleDraweeView.getHierarchy().setPlaceholderImage(null);
        }
        if (feedItemResourceHelper == null || feedItemResourceHelper.browseImageOnClick()) {
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = LayoutInflater.from(v.getContext());
                    final View overlay = inflater.inflate(R.layout.overlay_feed_imageswipe, null);
                    setOverlayContent(overlay, getItem(position), onItemClickListener);

                    new ImageViewer.Builder(v.getContext(), displayLinks).setStartPosition(position).setOverlayView(overlay).setImageChangeListener(new ImageViewer.OnImageChangeListener() {
                        @Override
                        public void onImageChange(int position) {
                            setOverlayContent(overlay, getItem(position), onItemClickListener);
                        }
                    }).show();
                }
            });
        } else {
            simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onFeedImageClick(feedItemResourceHelper.getFeedStoryType(),feedItemResourceHelper.getUrl(events.get(position)), events.get(position), feedItemResourceHelper.getTargetMember(events.get(position)));
                }
            });
        }

        return simpleDraweeView;
    }

}

