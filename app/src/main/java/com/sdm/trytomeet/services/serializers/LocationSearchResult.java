package com.sdm.trytomeet.services.serializers;

import com.google.gson.annotations.SerializedName;

public class LocationSearchResult {
    @SerializedName("lat")
    private Double latitude;

    @SerializedName("lng")
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
