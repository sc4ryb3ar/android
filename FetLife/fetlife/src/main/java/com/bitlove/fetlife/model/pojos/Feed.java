
package com.bitlove.fetlife.model.pojos;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Feed {

    @JsonProperty("stories")
    private List<Story> stories = new ArrayList<Story>();

    /**
     * 
     * @return
     *     The stories
     */
    @JsonProperty("stories")
    public List<Story> getStories() {
        return stories;
    }

    /**
     * 
     * @param stories
     *     The stories
     */
    @JsonProperty("stories")
    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

}
