package com.bitlove.fetlife.model.pojos.github;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Release {

    @JsonProperty("name")
    private String name;

    @JsonProperty("assets")
    private List<Asset> assets;

    public String getName() {
        return name;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }
}
