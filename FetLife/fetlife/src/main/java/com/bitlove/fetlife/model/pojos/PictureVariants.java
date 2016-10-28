
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PictureVariants {

    @JsonProperty("medium")
    private Medium medium;
    @JsonProperty("large")
    private Original large;
    @JsonProperty("huge")
    private Original huge;
    @JsonProperty("original")
    private Original original;

    public Original getHuge() {
        return huge;
    }

    public void setHuge(Original huge) {
        this.huge = huge;
    }

    public Original getOriginal() {
        return original;
    }

    public void setOriginal(Original original) {
        this.original = original;
    }

    public Original getLarge() {
        return large;
    }

    public void setLarge(Original large) {
        this.large = large;
    }

    /**
     * 
     * @return
     *     The medium
     */
    @JsonProperty("medium")
    public Medium getMedium() {
        return medium;
    }

    /**
     * 
     * @param medium
     *     The medium
     */
    @JsonProperty("medium")
    public void setMedium(Medium medium) {
        this.medium = medium;
    }

}
