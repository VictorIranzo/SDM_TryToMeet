package com.sdm.trytomeet.services.serializers;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbySearchResult {
    @SerializedName("vicinity")
    private String vicinity;

    @SerializedName("geometry")
    private GeometrySearchResult geometry;

    @SerializedName("name")
    private String name;

    @SerializedName("types")
    private List<String> types;

    public String getVicinity() {
        return vicinity;
    }

    public String getName() {
        return name;
    }

    public GeometrySearchResult getGeometry() {
        return geometry;
    }

    public List<String> getTypes() {
        return types;
    }
}
