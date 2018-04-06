package POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by adrymc96 on 20/03/18.
 */

@IgnoreExtraProperties
public class Event {
    public String name;
    public String description;
    // TODO: LUGAR PROPORCIONADO POR GOOGLE (ALGO PARA IDENTIFICARLO, NO TODA SU INFO)
    public Site site;
    public List<Date> possible_dates;
    public List<String> participants_id;
    public String creator_id;
    public String state;

    public Event(){}

    // This method should be properly adapted when using it
    public Event(String name, String description, List<Date> possible_dates,
                 List<String> participants_id, String creator_id, String state, Site site){
        this.name = name;
        this.description = description;
        this.possible_dates = possible_dates;
        this.participants_id = participants_id;
        this.creator_id = creator_id;
        this.state = state;
        this.site = site;
    }

}
