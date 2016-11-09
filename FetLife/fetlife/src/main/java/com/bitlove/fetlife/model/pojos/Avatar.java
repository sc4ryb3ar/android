package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Avatar {

    @JsonProperty("variants")
    private AvatarVariants variants;

    public AvatarVariants getVariants() {
        return variants;
    }

    public void setVariants(AvatarVariants variants) {
        this.variants = variants;
    }
}
