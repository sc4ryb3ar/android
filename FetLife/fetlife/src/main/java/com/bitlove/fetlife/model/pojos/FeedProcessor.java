package com.bitlove.fetlife.model.pojos;

import java.util.ArrayList;
import java.util.List;

public class FeedProcessor {

    public enum FeedStoryType {
        FRIENDS, LOVED, FOLLOWS, DEFAULT;
    };

    private Member displayMember;
    private Event templateEvent;
    private List<Event> events;
    private FeedStoryType storyType;

    public FeedProcessor(FeedStory feedStory) {
        events = feedStory.getEvents();
        if (events == null) {
            events = new ArrayList<>();
            return;
        }
        templateEvent = events.get(0);
        displayMember = templateEvent.getTarget().getMember();
        if (displayMember == null) {
            displayMember = templateEvent.getSecondaryTarget().getMember();
        }

        switch (feedStory.getName()) {
            case "friend_created":
                storyType = FeedStoryType.FRIENDS;
                break;
            case "like_created":
                storyType = FeedStoryType.LOVED;
                break;
            case "follow_created":
                storyType = FeedStoryType.FOLLOWS;
                break;
            default:
                storyType = FeedStoryType.DEFAULT;
                break;
        }
    }

    public Member getDisplayMember() {
        return displayMember;
    }

    public Event getTemplateEvent() {
        return templateEvent;
    }

    public List<Event> getEvents() {
        return events;
    }

    public FeedStoryType getStoryType() {
        return storyType;
    }

    public int getEventCount() {
        return events.size();
    }

}
