
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecondaryTarget {

    @JsonProperty("member")
    private Member member;
    @JsonProperty("picture")
    private Picture picture;

    /**
     *
     * @return
     *     The picture
     */
    @JsonProperty("picture")
    public Picture getPicture() {
        return picture;
    }

    /**
     *
     * @param picture
     *     The picture
     */
    @JsonProperty("picture")
    public void setPicture(Picture picture) {
        this.picture = picture;
    }
    /**
     * 
     * @return
     *     The member
     */
    @JsonProperty("member")
    public Member getMember() {
        return member;
    }

    /**
     * 
     * @param member
     *     The member
     */
    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
    }

}
