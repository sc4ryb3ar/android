package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

public class Event {

    @JsonProperty("id")
    public String id;

    @JsonProperty("secondary_target")
    private Target secondaryTarget;
    @JsonProperty("target")
    private Target target;

    public Target getSecondaryTarget() {
        return secondaryTarget;
    }

    public void setSecondaryTarget(Target secondaryTarget) {
        this.secondaryTarget = secondaryTarget;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

}
