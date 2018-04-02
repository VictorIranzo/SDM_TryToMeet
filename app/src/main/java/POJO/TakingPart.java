package POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by adrymc96 on 20/03/18.
 */

@IgnoreExtraProperties
public class TakingPart {
    public Map<String,InvitedTo> invitedTo;

    public TakingPart(){}
}