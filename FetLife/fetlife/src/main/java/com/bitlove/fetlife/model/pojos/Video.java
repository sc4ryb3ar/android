package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Video {

    @JsonProperty("id")
    private String id;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("member")
    private Member member;
    @JsonProperty("content_type")
    private String contentType;
    @JsonProperty("love_count")
    private Integer loveCount;
    @JsonProperty("is_loved_by_me")
    private Boolean isLovedByMe;
    @JsonProperty("comment_count")
    private Integer commentCount;
    @JsonProperty("video_url")
    private String videoUrl;
    @JsonProperty("thumbnail")
    private Thumbnail thumbnail;
    @JsonProperty("body")
    private String body;
    @JsonProperty("url")
    private String url;

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The createdAt
     */
    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The member
     */
    @JsonProperty("member")
    public Member getMember() {
        return member;
    }

    /**
     *
     * @param member
     * The member
     */
    @JsonProperty("member")
    public void setMember(Member member) {
        this.member = member;
    }

    /**
     *
     * @return
     * The contentType
     */
    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    /**
     *
     * @param contentType
     * The content_type
     */
    @JsonProperty("content_type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     *
     * @return
     * The loveCount
     */
    @JsonProperty("love_count")
    public Integer getLoveCount() {
        return loveCount;
    }

    /**
     *
     * @param loveCount
     * The love_count
     */
    @JsonProperty("love_count")
    public void setLoveCount(Integer loveCount) {
        this.loveCount = loveCount;
    }

    /**
     *
     * @return
     * The isLovedByMe
     */
    @JsonProperty("is_loved_by_me")
    public Boolean getIsLovedByMe() {
        return isLovedByMe;
    }

    /**
     *
     * @param isLovedByMe
     * The is_loved_by_me
     */
    @JsonProperty("is_loved_by_me")
    public void setIsLovedByMe(Boolean isLovedByMe) {
        this.isLovedByMe = isLovedByMe;
    }

    /**
     *
     * @return
     * The commentCount
     */
    @JsonProperty("comment_count")
    public Integer getCommentCount() {
        return commentCount;
    }

    /**
     *
     * @param commentCount
     * The comment_count
     */
    @JsonProperty("comment_count")
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    /**
     *
     * @return
     * The videoUrl
     */
    @JsonProperty("video_url")
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     *
     * @param videoUrl
     * The video_url
     */
    @JsonProperty("video_url")
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    /**
     *
     * @return
     * The thumbnail
     */
    @JsonProperty("thumbnail")
    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    /**
     *
     * @param thumbnail
     * The thumbnail
     */
    @JsonProperty("thumbnail")
    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     *
     * @return
     * The body
     */
    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    /**
     *
     * @param body
     * The body
     */
    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

    /**
     *
     * @return
     * The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

}