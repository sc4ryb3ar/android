package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMembership {

    @JsonProperty("id")
    private String id;
    @JsonProperty("group")
    private Group group;
    @JsonProperty("member")
    private Member member;
    @JsonProperty("created_at")
    private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

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
     * The group
     */
    @JsonProperty("group")
    public Group getGroup() {
        return group;
    }

    /**
     *
     * @param group
     * The group
     */
    @JsonProperty("group")
    public void setGroup(Group group) {
        this.group = group;
    }

}