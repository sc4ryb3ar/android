
package com.bitlove.fetlife.model.pojos;

import java.util.ArrayList;
import java.util.List;

import com.bitlove.fetlife.util.EnumUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Story {

    public static enum FeedStoryType {

        /*
        updated_about 1743
        status_comment_created 1513
        group_post_created 1073
        user_signed_up 696
        updated_fetish_status 309
        location_updated 276
        post_updated 262
        role_updated 257
        ds_relationship_updated 45
        ds_relationship_created 254
        event_created 77
        rsvp_updated 204
        nickname_updated 146
        video_created 139
        sexual_orientation_updated 118
        updated_websites 107
        relationship_created 215
        relationship_updated 80

        supported_fetlife 71
        vote_created 34
        sex_updated 26
        volunteered_to_be_group_leader 3
        improvement_comment_created 1
        invited_user_signed_up 1
        */

        LIKE_CREATED,
        FOLLOW_CREATED,
        COMMENT_CREATED,
        PICTURE_CREATED,
        FRIEND_CREATED,
        POST_CREATED,
        RSVP_CREATED,
        GROUP_MEMBERSHIP_CREATED,
        POST_COMMENT_CREATED,
        VIDEO_COMMENT_CREATED,
        PEOPLE_INTO_CREATED,

        PROMOTED_TO_GROUP_LEADER,
        GROUP_COMMENT_CREATED,
        WALL_POST_CREATED,
        PROFILE_UPDATED,
        STATUS_CREATED
    }

    @JsonProperty("name")
    private String name;
    @JsonProperty("events")
    private List<FeedEvent> events = new ArrayList<FeedEvent>();

    public FeedStoryType getType() {
        return EnumUtil.safeParse(FeedStoryType.class, name);
    }

    /**
     *
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The events
     */
    @JsonProperty("events")
    public List<FeedEvent> getEvents() {
        return events;
    }

    /**
     *
     * @param events
     *     The events
     */
    @JsonProperty("events")
    public void setEvents(List<FeedEvent> events) {
        this.events = events;
    }

}
