
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
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
    @JsonProperty("status")
    private Status status;
    @JsonProperty("wall_post")
    private WallPost wallPost;

    public WallPost getWallPost() {
        return wallPost;
    }

    public void setWallPost(WallPost wallPost) {
        this.wallPost = wallPost;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

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
