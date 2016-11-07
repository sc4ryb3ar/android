
package com.bitlove.fetlife.model.pojos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Story {

    @JsonProperty("name")
    private String name;
    @JsonProperty("events")
    private List<FeedEvent> events = new ArrayList<FeedEvent>();

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
