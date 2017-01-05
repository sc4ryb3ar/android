package com.bitlove.fetlife.model.pojos;

import android.content.Context;

import com.bitlove.fetlife.R;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PeopleInto {

    public enum Activity {
        _NO_STATUS_,
        GIVING,
        RECEIVING,
        WATCHING,
        WEARING,
        WATCHING_OTHERS_WEAR,
        EVERYTHING_TO_DO_WITH_IT;

        public static Activity fromString(String text) {
            if (text == null || text.trim().length() == 0) {
                return _NO_STATUS_;
            }
            try {
                return Activity.valueOf(text.toUpperCase().replaceAll(" ","_"));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public String toString(Context context) {
            switch (this) {
                case GIVING:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_giving);
                case RECEIVING:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_receiving);
                case WATCHING:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_watching);
                case WEARING:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_wearing);
                case WATCHING_OTHERS_WEAR:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_watching_others_wear);
                case EVERYTHING_TO_DO_WITH_IT:
                    return context.getResources().getString(R.string.pojo_value_fetish_status_everything_to_do_with_it);
                case _NO_STATUS_:
                    return "";
            }
            return null;
        }
    }

    public enum Status {

        INTO,
        CURIOUS_ABOUT,
        SOFT_LIMIT,
        HARD_LIMIT;

        public static Status fromString(String text) {
            try {
                return Status.valueOf(text.toUpperCase().replaceAll(" ","_"));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        public String toString(Context context, String nickName, String fetishName, String statusText) {
            switch (this) {
                case INTO:
                    return context.getResources().getString(R.string.pojo_value_fetish_activity_into, nickName, statusText, fetishName);
                case CURIOUS_ABOUT:
                    return context.getResources().getString(R.string.pojo_value_fetish_activity_curious_about, nickName, statusText, fetishName);
                case SOFT_LIMIT:
                    return context.getResources().getString(R.string.pojo_value_fetish_activity_soft_limit, nickName, statusText, fetishName);
                case HARD_LIMIT:
                    return context.getResources().getString(R.string.pojo_value_fetish_activity_hard_limit, nickName, statusText, fetishName);
            }
            return null;
        }

    }

    @JsonProperty("id")
    private String id;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("member")
    private Member member;
    @JsonProperty("content_type")
    private String contentType;
    @JsonProperty("fetish")
    private Fetish fetish;
    @JsonProperty("status")
    private String status;
    @JsonProperty("activity")
    private String activity;

    public Status getActivityEnum() {
        return Status.fromString(status);
    }

    public Activity getStatusEnum() {
        return Activity.fromString(activity);
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
     * The fetish
     */
    @JsonProperty("fetish")
    public Fetish getFetish() {
        return fetish;
    }

    /**
     *
     * @param fetish
     * The fetish
     */
    @JsonProperty("fetish")
    public void setFetish(Fetish fetish) {
        this.fetish = fetish;
    }

    /**
     *
     * @return
     * The status
     */
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The activity
     */
    @JsonProperty("activity")
    public String getActivity() {
        return activity;
    }

    /**
     *
     * @param activity
     * The activity
     */
    @JsonProperty("activity")
    public void setActivity(String activity) {
        this.activity = activity;
    }

}