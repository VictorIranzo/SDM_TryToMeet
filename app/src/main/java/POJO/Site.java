package POJO;

import com.google.android.gms.maps.model.Marker;

public class Site {
    public String name;
    public String description;

    public double latitude;
    public double longitude;

    public Site(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Site(){}
}
