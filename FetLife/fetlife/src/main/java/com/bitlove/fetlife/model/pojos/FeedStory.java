package com.bitlove.fetlife.model.pojos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.raizlabs.android.dbflow.structure.BaseModel;

public class FeedStory {

    @JsonProperty("name")
    private String name;

    @JsonProperty("events")
    private List<Event> events = new ArrayList<Event>();

    public String getName() {
        return name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
