
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecondaryTarget {

    @JsonProperty("member")
    private Member member;
    @JsonProperty("video")
    private Video video;
    @JsonProperty("picture")
    private Picture picture;
    @JsonProperty("writing")
    private Writing writing;
    @JsonProperty("group_post")
    private GroupPost groupPost;

    public GroupPost getGroupPost() {
        return groupPost;
    }

    public void setGroupPost(GroupPost groupPost) {
        this.groupPost = groupPost;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Writing getWriting() {
        return writing;
    }

    public void setWriting(Writing writing) {
        this.writing = writing;
    }

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
