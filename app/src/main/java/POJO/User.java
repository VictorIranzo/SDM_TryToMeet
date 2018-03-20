package POJO;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by adrymc96 on 20/03/18.
 */

@IgnoreExtraProperties
public class User {
    public String username;
    public String url;

    public User(){}

    public User(String username, String url){
        this.username = username;
        this.url = url;
    }

    public String toString(){
        return "Username: " + username + ", url: " + url;
    }
}
