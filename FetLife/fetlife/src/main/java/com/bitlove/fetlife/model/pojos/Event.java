
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public class Event {

    @JsonProperty("target")
    private Target target;
    @JsonProperty("secondary_target")
    private SecondaryTarget secondaryTarget;

    /**
     * 
     * @return
     *     The target
     */
    @JsonProperty("target")
    public Target getTarget() {
        return target;
    }

    /**
     * 
     * @param target
     *     The target
     */
    @JsonProperty("target")
    public void setTarget(Target target) {
        this.target = target;
    }

    /**
     * 
     * @return
     *     The secondaryTarget
     */
    @JsonProperty("secondary_target")
    public SecondaryTarget getSecondaryTarget() {
        return secondaryTarget;
    }

    /**
     * 
     * @param secondaryTarget
     *     The secondary_target
     */
    @JsonProperty("secondary_target")
    public void setSecondaryTarget(SecondaryTarget secondaryTarget) {
        this.secondaryTarget = secondaryTarget;
    }

}
