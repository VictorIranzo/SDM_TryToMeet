package com.sdm.trytomeet.helpers;

import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.EventWithKey;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;

import java.util.Calendar;

public class EventTransactionPendingToCanceled {

    // True if a transaction is done. If true, the event is not added to the list of events.
    public static boolean checkIfPassedDate(EventWithKey e){
        // If has no dates, the event is sent to cancelled events.
        if(e.possible_dates == null){
            EventFirebaseService.passEventPendingToCancel(e.event_id);
            return true;
        }

        // If it's in a different state of PENDING, no date has to be removed.
        if(!e.state.equals(Event.PENDING)) return false;

        Calendar cal = Calendar.getInstance();
        Date currentDate = new Date(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE));

        for (Date date: e.possible_dates) {
            // The current date is bigger than one of the pending dates
            if(currentDate.compareTo(date)>0){
                if(e.possible_dates.size() - 1 == 0){
                    EventFirebaseService.passEventPendingToCancel(e.event_id);
                    return true;
                }
                else{
                    EventFirebaseService.removePastVotedDate(e.event_id,date);
                }
            }
        }

        return false;
    }
}
