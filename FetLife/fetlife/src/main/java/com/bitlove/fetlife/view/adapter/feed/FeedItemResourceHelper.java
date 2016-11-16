package com.bitlove.fetlife.view.adapter.feed;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.FeedEvent;
import com.bitlove.fetlife.model.pojos.Picture;
import com.bitlove.fetlife.model.pojos.PictureVariantsInterface;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Rsvp;
import com.bitlove.fetlife.model.pojos.Story;
import com.bitlove.fetlife.util.DateUtil;
import com.bitlove.fetlife.util.EnumUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class FeedItemResourceHelper {

    private final Story.FeedStoryType feedStoryType;
    private final Story feedStory;
    private FetLifeApplication fetLifeApplication;

    FeedItemResourceHelper(FetLifeApplication fetLifeApplication, Story feedstory) {
        this.fetLifeApplication = fetLifeApplication;
        this.feedStoryType = feedstory.getType();
        this.feedStory = feedstory;
    }

    public Story.FeedStoryType getFeedStoryType() {
        return feedStoryType;
    }

    public String getHeader(List<FeedEvent> events) {
        int eventCount = events.size();
        FeedEvent feedEvent = events.get(0);
        switch (feedStoryType) {
            case POST_COMMENT_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_postcomment) : fetLifeApplication.getString(R.string.feed_title_new_postcomments, eventCount);
            case GROUP_MEMBERSHIP_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_group_join) : fetLifeApplication.getString(R.string.feed_title_group_joins, eventCount);
            case POST_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_post) : fetLifeApplication.getString(R.string.feed_title_new_posts, eventCount);
            case PICTURE_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_picture) : fetLifeApplication.getString(R.string.feed_title_new_pictures, eventCount);
            case LIKE_CREATED:
                FeedEvent firstEvent = feedStory.getEvents().get(0);
                if (firstEvent.getSecondaryTarget().getWriting() != null) {
                    return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_like_writing) : fetLifeApplication.getString(R.string.feed_title_like_writings, eventCount);
                } else if (firstEvent.getSecondaryTarget().getPicture() != null) {
                    return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_like_picture) : fetLifeApplication.getString(R.string.feed_title_like_pictures, eventCount);
                } else {
                    throw new IllegalArgumentException();
                }
            case FRIEND_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_friend) : fetLifeApplication.getString(R.string.feed_title_new_friends, eventCount);
            case COMMENT_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_comment) : fetLifeApplication.getString(R.string.feed_title_new_comments, eventCount);
            case FOLLOW_CREATED:
                return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_new_follow) : fetLifeApplication.getString(R.string.feed_title_new_follows, eventCount);
            case RSVP_CREATED:
                switch (feedEvent.getTarget().getRsvp().getRsvpStatus()) {
                    case YES:
                        return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_rsvp_yes) : fetLifeApplication.getString(R.string.feed_title_rsvps_yes, eventCount);
                    case MAYBE:
                        return eventCount == 1 ? fetLifeApplication.getString(R.string.feed_title_rsvp_maybe) : fetLifeApplication.getString(R.string.feed_title_rsvps_maybe, eventCount);
                    default:
                        return null;
                }
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
                case POST_COMMENT_CREATED:
                    return event.getTarget().getComment().getMember();
                case GROUP_MEMBERSHIP_CREATED:
                    return event.getTarget().getGroupMembership().getMember();
                case POST_CREATED:
                    return event.getTarget().getWriting().getMember();
                case PICTURE_CREATED:
                    return event.getTarget().getPicture().getMember();
                case LIKE_CREATED:
                    return event.getTarget().getLove().getMember();
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return event.getTarget().getRelation().getMember();
                case RSVP_CREATED:
                    return event.getTarget().getRsvp().getMember();
                case COMMENT_CREATED:
                    return event.getTarget().getComment().getMember();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public Picture getPicture(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case PICTURE_CREATED:
                    return feedEvent.getTarget().getPicture();
                case LIKE_CREATED:
                    Picture result = feedEvent.getSecondaryTarget().getPicture();
                    if (result != null) {
                        return result;
                    }
                    return  feedEvent.getSecondaryTarget().getWriting().getMember().getAvatarPicture();
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return feedEvent.getSecondaryTarget().getMember().getAvatarPicture();
                case COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getPicture();
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
                case POST_COMMENT_CREATED:
                    createdAt = feedEvent.getTarget().getComment().getCreatedAt();
                    break;
                case GROUP_MEMBERSHIP_CREATED:
                    createdAt = feedEvent.getTarget().getGroupMembership().getCreatedAt();
                    break;
                case POST_CREATED:
                    createdAt = feedEvent.getTarget().getWriting().getCreatedAt();
                    break;
                case PICTURE_CREATED:
                    createdAt = feedEvent.getTarget().getPicture().getCreatedAt();
                    break;
                case LIKE_CREATED:
                    createdAt = feedEvent.getTarget().getLove().getCreatedAt();
                    break;
                case COMMENT_CREATED:
                    createdAt = feedEvent.getTarget().getComment().getCreatedAt();
                    break;
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    createdAt = feedEvent.getTarget().getRelation().getCreatedAt();
                    break;
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

    public String getUrl(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case POST_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getWriting().getUrl();
                case GROUP_MEMBERSHIP_CREATED:
                    return feedEvent.getTarget().getGroupMembership().getGroup().getUrl();
                case POST_CREATED:
                    return feedEvent.getTarget().getWriting().getUrl();
                case PICTURE_CREATED:
                    return feedEvent.getTarget().getPicture().getUrl();
                case LIKE_CREATED:
                    if (feedEvent.getSecondaryTarget().getPicture() != null) {
                        return feedEvent.getSecondaryTarget().getPicture().getUrl();
                    } else if (feedEvent.getSecondaryTarget().getWriting() != null) {
                        return feedEvent.getSecondaryTarget().getWriting().getUrl();
                    } else {
                        return null;
                    }
                case COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getPicture().getUrl();
                case RSVP_CREATED:
                    return feedEvent.getTarget().getRsvp().getEvent().getUrl();
                case FRIEND_CREATED:
                case FOLLOW_CREATED:
                    return feedEvent.getSecondaryTarget().getMember().getLink();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getItemTitle(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case LIKE_CREATED:
                    return feedEvent.getSecondaryTarget().getWriting().getTitle();
                case POST_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getWriting().getTitle();
                case GROUP_MEMBERSHIP_CREATED:
                    return feedEvent.getTarget().getGroupMembership().getGroup().getName();
                case POST_CREATED:
                    return feedEvent.getTarget().getWriting().getTitle();
                case FOLLOW_CREATED:
                case FRIEND_CREATED:
                    return feedEvent.getSecondaryTarget().getMember().getNickname();
                case RSVP_CREATED:
                    return feedEvent.getTarget().getRsvp().getEvent().getName();
                case COMMENT_CREATED:
                    return null;
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getItemBody(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case LIKE_CREATED:
                    return feedEvent.getSecondaryTarget().getWriting().getBody();
                case POST_COMMENT_CREATED:
                    return feedEvent.getSecondaryTarget().getWriting().getBody();
                case GROUP_MEMBERSHIP_CREATED:
                    return feedEvent.getTarget().getGroupMembership().getGroup().getDescription();
                case POST_CREATED:
                    return feedEvent.getTarget().getWriting().getBody();
                case FOLLOW_CREATED:
                case FRIEND_CREATED:
                    return feedEvent.getSecondaryTarget().getMember().getMetaInfo();
                case RSVP_CREATED:
                    return feedEvent.getTarget().getRsvp().getEvent().getLocation() + " - " + feedEvent.getTarget().getRsvp().getEvent().getAddress();
                case COMMENT_CREATED:
                    return feedEvent.getTarget().getComment().getBody();
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public String getItemCaption(FeedEvent feedEvent) {
        try {
            switch (feedStoryType) {
                case POST_COMMENT_CREATED:
                    return feedEvent.getTarget().getComment().getBody();
                case GROUP_MEMBERSHIP_CREATED:
                    return fetLifeApplication.getString(R.string.feed_caption_member_count,feedEvent.getTarget().getGroupMembership().getGroup().getMemberCount());
                case RSVP_CREATED:
                    return SimpleDateFormat.getDateTimeInstance().format(DateUtil.parseDate(feedEvent.getTarget().getRsvp().getEvent().getStartDateTime()));
                case FOLLOW_CREATED:
                case FRIEND_CREATED:
                case COMMENT_CREATED:
                default:
                    return null;
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public boolean imageOnlyListItems() {
        switch (feedStoryType) {
            case PICTURE_CREATED:
                return (feedStory.getEvents().get(0).getSecondaryTarget().getPicture() != null);
            case LIKE_CREATED:
                return true;
            default:
                return false;
        }
    }

    public boolean listOnly() {
        switch (feedStoryType) {
            case PICTURE_CREATED:
                return (feedStory.getEvents().get(0).getSecondaryTarget().getPicture() == null);
            case POST_COMMENT_CREATED:
            case GROUP_MEMBERSHIP_CREATED:
            case POST_CREATED:
            case COMMENT_CREATED:
            case RSVP_CREATED:
                return true;
            default:
                return false;
        }
    }

    public boolean browseImageOnClick() {
        switch (feedStoryType) {
            case COMMENT_CREATED:
            case PICTURE_CREATED:
            case LIKE_CREATED:
                return (feedStory.getEvents().get(0).getSecondaryTarget().getPicture() != null);
            default:
                return false;
        }
    }

    public boolean useImagePlaceHolder(FeedEvent feedEvent) {
        switch (feedStoryType) {
            case FRIEND_CREATED:
            case FOLLOW_CREATED:
                return true;
            default:
                return false;
        }
    }
}
