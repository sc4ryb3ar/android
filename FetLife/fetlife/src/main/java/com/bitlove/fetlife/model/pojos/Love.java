
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Love {

    @JsonProperty("target_type")
    private String targetType;
    @JsonProperty("target_id")
    private String targetId;
    @JsonProperty("member")
    private Member member;
    @JsonProperty("id")
    private String id;

    /**
     *
     * @return
     *     The targetType
     */
    @JsonProperty("target_type")
    public String getTargetType() {
        return targetType;
    }

    /**
     *
     * @param targetType
     *     The target_type
     */
    @JsonProperty("target_type")
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    /**
     *
     * @return
     *     The targetId
     */
    @JsonProperty("target_id")
    public String getTargetId() {
        return targetId;
    }

    /**
     *
     * @param targetId
     *     The target_id
     */
    @JsonProperty("target_id")
    public void setTargetId(String targetId) {
        this.targetId = targetId;
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

}
