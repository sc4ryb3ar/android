package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Feed {

    private List<FeedStory> stories = new ArrayList<FeedStory>();

    /**
     *
     * @return
     * The stories
     */
    @JsonProperty("stories")
    public List<FeedStory> getStories() {
        return stories;
    }

    /**
     *
     * @param stories
     * The stories
     */
    @JsonProperty("stories")
    public void setStories(List<FeedStory> stories) {
        this.stories = stories;
    }
}

