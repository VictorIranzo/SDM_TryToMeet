package com.sdm.trytomeet.POJO;

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

    public Site(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Site(){}

    @Override
    public boolean equals(Object o){
        return o instanceof Site && ((Site)o).name.equals(this.name);
    }
}
