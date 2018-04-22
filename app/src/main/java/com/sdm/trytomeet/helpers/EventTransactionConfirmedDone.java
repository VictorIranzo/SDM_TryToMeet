package com.sdm.trytomeet.helpers;

import com.sdm.trytomeet.POJO.Date;
import com.sdm.trytomeet.POJO.Event;
import com.sdm.trytomeet.POJO.EventWithKey;
import com.sdm.trytomeet.persistence.server.EventFirebaseService;

import java.util.Calendar;

public class EventTransactionConfirmedDone {
    // True if a transaction is done. If true, the event is not added to the list of events.
    public static boolean checkConfirmedDate(EventWithKey e){
        if(e.confirmed_date == null) return false;

        Calendar cal = Calendar.getInstance();
        Date currentDate = new Date(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE));

        if(currentDate.compareTo(e.confirmed_date)>0){
            EventFirebaseService.passEventConfirmedToDone(e.event_id);
            return true;
        }

        return false;
    }
}
