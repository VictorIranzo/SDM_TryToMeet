package com.sdm.trytomeet.fragments.Events;

import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.EventWithKey;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;

public class PendingVoteEventListFragment extends EventListFragment {
    @Override
    public void addEventToList(String event_id, Event e){
        if(e.state.equals(Event.PENDING)) {
            events.add(new EventWithKey(event_id,e));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void getUserEvents(){
        EventFirebaseService.getUserEventsPendingVote(user_id,this);
    }
}
