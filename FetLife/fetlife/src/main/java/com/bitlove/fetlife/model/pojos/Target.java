
package com.bitlove.fetlife.model.pojos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Target {

    @JsonProperty("relation")
    private Relation relation;
    @JsonProperty("love")
    private Love love;
    @JsonProperty("rsvp")
    private Rsvp rsvp;

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
