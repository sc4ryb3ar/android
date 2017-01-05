
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Target {

    @JsonProperty("relation")
    private Relation relation;
    @JsonProperty("love")
    private Love love;
    @JsonProperty("rsvp")
    private Rsvp rsvp;
    @JsonProperty("comment")
    private Comment comment;
    @JsonProperty("picture")
    private Picture picture;
    @JsonProperty("writing")
    private Writing writing;
    @JsonProperty("group_membership")
    private GroupMembership groupMembership;
    @JsonProperty("people_into")
    private PeopleInto peopleInto;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("group_post")
    private GroupPost groupPost;
    @JsonProperty("wall_post")
    private WallPost wallPost;

    public GroupPost getGroupPost() {
        return groupPost;
    }

    public void setGroupPost(GroupPost groupPost) {
        this.groupPost = groupPost;
    }

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

    public PeopleInto getPeopleInto() {
        return peopleInto;
    }

    public void setPeopleInto(PeopleInto peopleInto) {
        this.peopleInto = peopleInto;
    }

    public GroupMembership getGroupMembership() {
        return groupMembership;
    }

    public void setGroupMembership(GroupMembership groupMembership) {
        this.groupMembership = groupMembership;
    }

    public Writing getWriting() {
        return writing;
    }

    public void setWriting(Writing writing) {
        this.writing = writing;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    /**
     *
     * @return
     *     The love
     */
    @JsonProperty("love")
    public Love getLove() {
        return love;
    }

    /**
     *
     * @param love
     *     The love
     */
    @JsonProperty("love")
    public void setLove(Love love) {
        this.love = love;
    }
    /**
     *
     * @return
     *     The relation
     */
    @JsonProperty("relation")
    public Relation getRelation() {
        return relation;
    }

    /**
     *
     * @param relation
     *     The relation
     */
    @JsonProperty("relation")
    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    @JsonProperty("rsvp")
    public Rsvp getRsvp() {
        return rsvp;
    }

    @JsonProperty("rsvp")
    public void setRsvp(Rsvp rsvp) {
        this.rsvp = rsvp;
    }
}
