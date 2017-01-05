
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PictureVariants implements PictureVariantsInterface{

    @JsonProperty("medium")
    private PictureVariant medium;
    @JsonProperty("large")
    private PictureVariant large;
    @JsonProperty("huge")
    private PictureVariant huge;
    @JsonProperty("original")
    private PictureVariant original;
    @JsonProperty("80")
    private PictureVariant _80;
    @JsonProperty("150")
    private PictureVariant _150;
    @JsonProperty("345")
    private PictureVariant _345;

    @JsonProperty("80")
    public PictureVariant get80() {
        return _80;
    }

    @JsonProperty("80")
    public void set80(PictureVariant _80) {
        this._80 = _80;
    }

    @JsonProperty("150")
    public PictureVariant get150() {
        return _150;
    }

    @JsonProperty("150")
    public void set150(PictureVariant _150) {
        this._150 = _150;
    }

    @JsonProperty("345")
    public PictureVariant get345() {
        return _345;
    }

    @JsonProperty("345")
    public void set345(PictureVariant _345) {
        this._345 = _345;
    }

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
