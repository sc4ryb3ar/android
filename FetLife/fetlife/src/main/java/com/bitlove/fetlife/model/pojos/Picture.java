
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Picture {

    @JsonProperty("variants")
    private PictureVariants variants;
    @JsonProperty("url")
    private String url;
    @JsonProperty("member")
    private Member member;
    @JsonProperty("love_count")
    private int loveCount;
    @JsonProperty("is_loved_by_me")
    private boolean isLovedByMe;
    @JsonProperty("id")
    private String id;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("content_type")
    private String contentType;
    @JsonProperty("comment_count")
    private int commentCount;
    @JsonProperty("body")
    private String body;

    /**
     *
     * @return
     *     The variants
     */
    @JsonProperty("variants")
    public PictureVariants getVariants() {
        return variants;
    }

    /**
     *
     * @param variants
     *     The variants
     */
    @JsonProperty("variants")
    public void setVariants(PictureVariants variants) {
        this.variants = variants;
    }

    /**
     *
     * @return
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
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

    /**
     *
     * @return
     *     The loveCount
     */
    @JsonProperty("love_count")
    public int getLoveCount() {
        return loveCount;
    }

    /**
     *
     * @param loveCount
     *     The love_count
     */
    @JsonProperty("love_count")
    public void setLoveCount(int loveCount) {
        this.loveCount = loveCount;
    }

    /**
     *
     * @return
     *     The isLovedByMe
     */
    @JsonProperty("is_loved_by_me")
    public boolean isIsLovedByMe() {
        return isLovedByMe;
    }

    /**
     *
     * @param isLovedByMe
     *     The is_loved_by_me
     */
    @JsonProperty("is_loved_by_me")
    public void setIsLovedByMe(boolean isLovedByMe) {
        this.isLovedByMe = isLovedByMe;
    }

    /**
     *
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The createdAt
     */
    @JsonProperty("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     *     The created_at
     */
    @JsonProperty("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     *     The contentType
     */
    @JsonProperty("content_type")
    public String getContentType() {
        return contentType;
    }

    /**
     *
     * @param contentType
     *     The content_type
     */
    @JsonProperty("content_type")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     *
     * @return
     *     The commentCount
     */
    @JsonProperty("comment_count")
    public int getCommentCount() {
        return commentCount;
    }

    /**
     *
     * @param commentCount
     *     The comment_count
     */
    @JsonProperty("comment_count")
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    /**
     *
     * @return
     *     The body
     */
    @JsonProperty("body")
    public String getBody() {
        return body;
    }

    /**
     *
     * @param body
     *     The body
     */
    @JsonProperty("body")
    public void setBody(String body) {
        this.body = body;
    }

}
