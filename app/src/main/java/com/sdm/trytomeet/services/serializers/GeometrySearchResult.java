package com.sdm.trytomeet.services.serializers;

import com.google.gson.annotations.SerializedName;

public class GeometrySearchResult {
    @SerializedName("location")
    private LocationSearchResult location;

    public LocationSearchResult getLocation() {
        return location;
    }
}
