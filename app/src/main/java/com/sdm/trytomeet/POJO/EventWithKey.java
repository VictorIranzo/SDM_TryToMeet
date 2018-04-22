package com.sdm.trytomeet.POJO;

public class EventWithKey extends Event{
    public String event_id;

    public EventWithKey(String event_id, Event event)
    {
        super(event.name,event.description,event.site,event.possible_dates,event.participants_id,event.creator_id,
                event.state, event.confirmed_date);
        this.event_id = event_id;
    }
}
