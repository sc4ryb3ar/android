package com.bitlove.fetlife.event;

import com.bitlove.fetlife.model.pojos.fetlife.json.Event;

import java.util.Collection;
import java.util.List;

public class EventByLocationRetrievedEvent {

    private List<Event> events;

    public EventByLocationRetrievedEvent(List<Event> events) {
       this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
