package POJO;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by adrymc96 on 20/03/18.
 */

@IgnoreExtraProperties
public class Event {
    public String name;
    public String description;
    // TODO: fecha
    public String coordenates;
    public String state;

    public Event(){}

    // This method should be properly adapted when using it
    public Event(String name){
        this.name = name;
    }

}
