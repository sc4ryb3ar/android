
package com.bitlove.fetlife.model.pojos;

import java.util.ArrayList;
import java.util.List;

import com.bitlove.fetlife.util.EnumUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Story {

    public static enum FeedStoryType {

        LIKE_CREATED,
        FOLLOW_CREATED,
        COMMENT_CREATED,
        PICTURE_CREATED,
        FRIEND_CREATED,
        POST_CREATED,
        RSVP_CREATED,
        GROUP_MEMBERSHIP_CREATED,
        GROUP_COMMENT_CREATED,
        POST_COMMENT_CREATED,
        VIDEO_COMMENT_CREATED,
        PEOPLE_INTO_CREATED,
        STATUS_CREATED,

        PROMOTED_TO_GROUP_LEADER,
        WALL_POST_CREATED,
        PROFILE_UPDATED,
        DS_RELATIONSHIP_STATUS_UPDATED
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
