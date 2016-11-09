package com.bitlove.fetlife.view.adapter.feed;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.FeedEvent;
import com.bitlove.fetlife.model.pojos.PictureInterface;
import com.bitlove.fetlife.model.pojos.PictureVariantsInterface;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Story;
import com.bitlove.fetlife.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class FeedItemResourceHelper {

    private final Story.FeedStoryType feedStoryType;
    private FetLifeApplication fetLifeApplication;

    FeedItemResourceHelper(FetLifeApplication fetLifeApplication, Story.FeedStoryType feedStoryType) {
        this.fetLifeApplication = fetLifeApplication;
        this.feedStoryType = feedStoryType;
    }

    public String getTitle(int eventCount) {
        switch (feedStoryType) {
            case LIKE_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_like_picture) : fetLifeApplication.getString(R.string.feed_title_like_pictures, eventCount);
            case FRIEND_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_friend) : fetLifeApplication.getString(R.string.feed_title_new_friends, eventCount);
            case FOLLOW_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_follow) : fetLifeApplication.getString(R.string.feed_title_new_follows, eventCount);
            default:
                return null;
        }
    }

    public boolean getExpandPreference() {
        int preferenceResource = -1;
        switch (feedStoryType) {
            case LIKE_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_like;
                break;
            case FRIEND_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_relation;
                break;
            case FOLLOW_CREATED:
                preferenceResource = R.string.settings_key_feed_auto_expand_relation;
                break;
        }
        if (preferenceResource > 0) {
            return fetLifeApplication.getUserSessionManager().getActiveUserPreferences().getBoolean(fetLifeApplication.getString(preferenceResource),false);
        } else {
            return true;
        }
    }

    public Member getMember(List<FeedEvent> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }
        FeedEvent event = events.get(0);
        try {
            switch (feedStoryType) {
                case LIKE_CREATED:
                    return event.getTarget().getLove().getMember();
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return event.getTarget().getRelation().getMember();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public PictureInterface getPicture(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case LIKE_CREATED:
                    return feedEvent.getSecondaryTarget().getPicture();
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return feedEvent.getSecondaryTarget().getMember().getAvatarPicture();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getCreatedAt(FeedEvent feedEvent) {
        try {
            String createdAt;
            switch (feedStoryType) {
                case LIKE_CREATED:
                    createdAt = feedEvent.getSecondaryTarget().getPicture().getCreatedAt();
                    break;
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                default:
                    return null;
            }
            if (createdAt !=  null) {
                return SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(createdAt));
            } else {
                return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }
}
