package POJO;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by adrymc96 on 31/03/18.
 */

@IgnoreExtraProperties
public class InvitedTo {
    public String event_id;

    public InvitedTo() {}

    public InvitedTo(String event_id){
        this.event_id = event_id;
    }
}
