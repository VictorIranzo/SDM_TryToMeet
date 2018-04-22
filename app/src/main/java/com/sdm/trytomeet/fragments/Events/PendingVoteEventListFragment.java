package com.sdm.trytomeet.fragments.Events;

import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.EventWithKey;
import com.sdm.trytomeet.helpers.EventTransactionPendingToCanceled;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;

public class PendingVoteEventListFragment extends EventListFragment {
    @Override
    public void addEventToList(String event_id, Event e){
        if(e.state.equals(Event.PENDING)) {
            EventWithKey eventWithKey = new EventWithKey(event_id,e);

            boolean notAdd = EventTransactionPendingToCanceled.checkIfPassedDate(eventWithKey);

            if(notAdd) return;

            events.add(eventWithKey);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void getUserEvents(){
        EventFirebaseService.getUserEventsPendingVote(user_id,this);
    }
}
