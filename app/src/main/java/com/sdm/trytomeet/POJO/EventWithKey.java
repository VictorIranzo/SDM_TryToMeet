package com.sdm.trytomeet.POJO;

public class EventWithKey extends Event{
    public String event_id;

    public EventWithKey(String event_id, Event event)
    {
        super(event.name,event.description, event.image);
        this.event_id = event_id;
    }
}
