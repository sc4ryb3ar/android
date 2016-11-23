package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
public class Thumbnail {

    @JsonProperty("variants")
    private PictureVariants variants;

    /**
     *
     * @return
     * The variants
     */
    @JsonProperty("variants")
    public PictureVariants getVariants() {
        return variants;
    }

    /**
     *
     * @param variants
     * The variants
     */
    @JsonProperty("variants")
    public void setVariants(PictureVariants variants) {
        this.variants = variants;
    }

    public Picture getAsPicture(Member member) {
        PictureVariants pictureVariants = new PictureVariants();

        PictureVariant pictureVariant = new PictureVariant();
        pictureVariant.setUrl(variants.get150().getUrl());
        pictureVariants.setMedium(pictureVariant);

        pictureVariant = new PictureVariant();
        pictureVariant.setUrl(variants.get345().getUrl());
        pictureVariants.setLarge(pictureVariant);

        Picture picture = new Picture();
        picture.setMember(member);
        picture.setVariants(pictureVariants);
        return picture;
    }

}
