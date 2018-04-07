package com.sdm.trytomeet.services.serializers;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbySearchResults {
    @SerializedName("results")
    private List<NearbySearchResult> results;

    public List<NearbySearchResult> getResults() {
        return results;
    }
}
