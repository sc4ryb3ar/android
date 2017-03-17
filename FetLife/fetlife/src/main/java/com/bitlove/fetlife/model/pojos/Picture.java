
package com.bitlove.fetlife.model.pojos;

import android.text.Html;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Table(database = FetLifeDatabase.class)
public class Picture extends BaseModel {

    @JsonProperty("variants")
    private PictureVariants variants;

    @Column
    @JsonProperty("url")
    private String url;

    @JsonProperty("member")
    private Member member;

    @Column
    @JsonIgnore
    private String memberId;

    @Column
    @JsonProperty("love_count")
    private int loveCount;

    @Column
    @JsonProperty("is_loved_by_me")
    private boolean isLovedByMe;

    @Column
    @PrimaryKey(autoincrement = false)
    @JsonProperty("id")
    private String id;

    @Column
    @JsonProperty("created_at")
    private String createdAt;

    @Column
    @JsonIgnore
    private long date;

    @Column
    @JsonProperty("content_type")
    private String contentType;

    @Column
    @JsonProperty("comment_count")
    private int commentCount;

    @Column
    @JsonProperty("body")
    private String body;

    @Column
    private String thumbUrl;

    @Column
    private String displayUrl;

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
        if (variants != null) {
            setThumbUrl(variants.getLargeUrl());
            setDisplayUrl(variants.getHugeUrl());
        }
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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
        if (member == null) {
            member = Member.loadMember(memberId);
        }
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
        if (member != null) {
            member.save();
            setMemberId(member.getId());
        }
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

    @JsonProperty("is_loved_by_me")
    public boolean isLovedByMe() {
        return isLovedByMe;
    }

    @JsonProperty("is_loved_by_me")
    public void setLovedByMe(boolean lovedByMe) {
        isLovedByMe = lovedByMe;
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
        if (createdAt != null) {
            try {
                setDate(DateUtil.parseDate(createdAt));
            } catch (Exception e) {
            }
        }
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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

    public static String getFormattedBody(String body) {
        try {
            return Html.fromHtml(body).toString();
        } catch (Throwable t) {
            return body;
        }
    }

    public static void startLoveCallWithObserver(final FetLifeApplication fetLifeApplication, final Picture picture, final boolean loved) {
        final String action = loved ? FetLifeApiIntentService.ACTION_APICALL_ADD_LOVE : FetLifeApiIntentService.ACTION_APICALL_REMOVE_LOVE;
        fetLifeApplication.getEventBus().register(new LoveImageCallObserver(fetLifeApplication, action, picture, loved));
        FetLifeApiIntentService.startApiCall(fetLifeApplication, action, picture.getId(), picture.getContentType());
    }

    private static class LoveImageCallObserver {

        private final FetLifeApplication fetLifeApplication;
        String action;
        Picture picture;
        boolean loved;

        LoveImageCallObserver(FetLifeApplication fetLifeApplication,String action, Picture picture, boolean loved) {
            this.action = action;
            this.picture = picture;
            this.loved = loved;
            this.fetLifeApplication = fetLifeApplication;
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onResourceListCallFinished(ServiceCallFinishedEvent serviceCallFinishedEvent) {
            if (serviceCallFinishedEvent.getServiceCallAction().equals(action) && checkParams(serviceCallFinishedEvent.getParams())) {
                fetLifeApplication.getEventBus().unregister(this);
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onResourceListCallFailed(ServiceCallFailedEvent serviceCallFailedEvent) {
            if (serviceCallFailedEvent.getServiceCallAction().equals(action) && checkParams(serviceCallFailedEvent.getParams())) {
                picture.setLovedByMe(!loved);
                fetLifeApplication.getEventBus().unregister(this);
            }
        }
        private boolean checkParams(String... params) {
            if (params == null || params.length != 2) {
                return false;
            }
            return picture.getId().equals(params[0]) && picture.getContentType().equals(params[1]);
        }
    }

}
