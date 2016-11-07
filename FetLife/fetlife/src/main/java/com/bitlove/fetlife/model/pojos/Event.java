
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {

    @JsonProperty("id")
    private String id;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("member")
    private Member member;
    @JsonProperty("content_type")
    private String contentType;
    @JsonProperty("name")
    private String name;
    @JsonProperty("tagline")
    private String tagline;
    @JsonProperty("description")
    private String description;
    @JsonProperty("start_date_time")
    private String startDateTime;
    @JsonProperty("end_date_time")
    private String endDateTime;
    @JsonProperty("location")
    private String location;
    @JsonProperty("address")
    private String address;
    @JsonProperty("cost")
    private String cost;
    @JsonProperty("dress_code")
    private String dressCode;

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
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The tagline
     */
    @JsonProperty("tagline")
    public String getTagline() {
        return tagline;
    }

    /**
     *
     * @param tagline
     *     The tagline
     */
    @JsonProperty("tagline")
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    /**
     *
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     *     The startDateTime
     */
    @JsonProperty("start_date_time")
    public String getStartDateTime() {
        return startDateTime;
    }

    /**
     *
     * @param startDateTime
     *     The start_date_time
     */
    @JsonProperty("start_date_time")
    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    /**
     *
     * @return
     *     The endDateTime
     */
    @JsonProperty("end_date_time")
    public String getEndDateTime() {
        return endDateTime;
    }

    /**
     *
     * @param endDateTime
     *     The end_date_time
     */
    @JsonProperty("end_date_time")
    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
     *
     * @return
     *     The location
     */
    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    /**
     *
     * @param location
     *     The location
     */
    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return
     *     The address
     */
    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    /**
     *
     * @param address
     *     The address
     */
    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return
     *     The cost
     */
    @JsonProperty("cost")
    public String getCost() {
        return cost;
    }

    /**
     *
     * @param cost
     *     The cost
     */
    @JsonProperty("cost")
    public void setCost(String cost) {
        this.cost = cost;
    }

    /**
     *
     * @return
     *     The dressCode
     */
    @JsonProperty("dress_code")
    public String getDressCode() {
        return dressCode;
    }

    /**
     *
     * @param dressCode
     *     The dress_code
     */
    @JsonProperty("dress_code")
    public void setDressCode(String dressCode) {
        this.dressCode = dressCode;
    }

}
