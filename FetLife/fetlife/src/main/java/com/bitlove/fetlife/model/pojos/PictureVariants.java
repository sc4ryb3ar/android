
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PictureVariants implements PictureVariantsInterface{

    @JsonProperty("medium")
    private PictureVariant medium;
    @JsonProperty("large")
    private PictureVariant large;
    @JsonProperty("huge")
    private PictureVariant huge;
    @JsonProperty("original")
    private PictureVariant original;

    public PictureVariant getHuge() {
        return huge;
    }

    public void setHuge(PictureVariant huge) {
        this.huge = huge;
    }

    public PictureVariant getOriginal() {
        return original;
    }

    public void setOriginal(PictureVariant original) {
        this.original = original;
    }

    public PictureVariant getLarge() {
        return large;
    }

    public void setLarge(PictureVariant large) {
        this.large = large;
    }

    /**
     *
     * @return
     *     The medium
     */
    @JsonProperty("medium")
    public PictureVariant getMedium() {
        return medium;
    }

    /**
     *
     * @param medium
     *     The medium
     */
    @JsonProperty("medium")
    public void setMedium(PictureVariant medium) {
        this.medium = medium;
    }

    @Override
    public String getMediumUrl() {
        return medium != null ? medium.getUrl() : null;
    }

    @Override
    public String getLargeUrl() {
        return large != null ? large.getUrl() : null;
    }

    @Override
    public String getHugeUrl() {
        return huge != null ? huge.getUrl() : null;
    }
}
