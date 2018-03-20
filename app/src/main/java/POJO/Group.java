package POJO;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by adrymc96 on 20/03/18.
 */

@IgnoreExtraProperties
public class Group {
    public List<String> members;

    public Group(){}

    public Group(List<String> members){
        this.members = members;
    }

    public String toString(){
        return "Members: " + members;
    }
}
