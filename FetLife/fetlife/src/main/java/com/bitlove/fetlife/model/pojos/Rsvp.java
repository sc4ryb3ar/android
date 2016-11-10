
package com.bitlove.fetlife.model.pojos;

import com.bitlove.fetlife.util.EnumUtil;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rsvp {

    public enum RsvpStatus {
        YES,
        MAYBE
    }

    @JsonProperty("id")
    private String id;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("member")
    private Member member;
    @JsonProperty("content_type")
    private String contentType;
    @JsonProperty("event")
    private Event event;
    @JsonProperty("status")
    private String status;

    public RsvpStatus getRsvpStatus() {
        return EnumUtil.safeParse(RsvpStatus.class, status);
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
     *     The event
     */
    @JsonProperty("event")
    public Event getEvent() {
        return event;
    }

    /**
     *
     * @param event
     *     The event
     */
    @JsonProperty("event")
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     *
     * @return
     *     The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

}
