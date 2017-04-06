package com.bitlove.fetlife.model.pojos;

import com.bitlove.fetlife.model.db.FetLifeDatabase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Arrays;
import java.util.List;

//TODO: clean up the POJOs and define relations
@Table(database = FetLifeDatabase.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member extends BaseModel {

    public static final String VALUE_FRIEND = "friend";
    public static final String VALUE_FRIEND_WITHOUT_FOLLOWING = "friend_without_following";
    public static final String VALUE_FOLLOWING = "following";
    public static final String VALUE_FRIEND_REQUEST_PENDING = "friend_request_pending";
    public static final String VALUE_FRIEND_REQUEST_SENT = "friend_request_sent";
    public static final String VALUE_FOLLOWING_FRIEND_REQUEST_SENT = "following_friend_request_sent";
    public static final String VALUE_FOLLOWING_FRIEND_REQUEST_PENDING = "following_friend_request_pending";

    private static final String SEPARATOR_LOOKING_FOR = ";";

    @JsonProperty("id")
    @Column
    @PrimaryKey(autoincrement = false)
    private String id;

    @JsonProperty("is_followable")
    @Column
    private boolean followable;

    @JsonProperty("relation_with_me")
    @Column
    private String relationWithMe;

    @JsonProperty("sexual_orientation")
    @Column
    private String sexualOrientation;

    @JsonProperty("looking_for")
    private List<String> lookingFor;

    @Column
    private String lookingForRawString;

    @JsonProperty("country")
    @Column
    private String country;

    @JsonProperty("city")
    @Column
    private String city;

    @JsonProperty("administrative_area")
    @Column
    private String administrativeArea;

    @JsonProperty("nickname")
    @Column
    private String nickname;

    @JsonProperty("about")
    @Column
    private String about;

    @JsonProperty("notification_token")
    @Column
    private String notificationToken;

    @JsonProperty("meta_line")
    @Column
    private String metaInfo;

    @JsonProperty("avatar")
    private Avatar avatar;

    @JsonProperty("url")
    @Column
    private String link;

    @JsonIgnore
    @Column
    private String avatarLink;

    @JsonIgnore
    @Column
    private String refreshToken;

    @JsonIgnore
    @Column
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static Member loadMember(String memberId) {
        Member member = new Select().from(Member.class).where(Member_Table.id.is(memberId)).querySingle();
        if (member == null) {
            return null;
        }
        return member;
    }

    public boolean isFollowable() {
        return followable;
    }

    public void setFollowable(boolean followable) {
        this.followable = followable;
    }

    public String getRelationWithMe() {
        return relationWithMe;
    }

    public void setRelationWithMe(String relationWithMe) {
        this.relationWithMe = relationWithMe;
    }

    public String getSexualOrientation() {
        return sexualOrientation;
    }

    public void setSexualOrientation(String sexualOrientation) {
        this.sexualOrientation = sexualOrientation;
    }

    public List<String> getLookingFor() {
        if (lookingFor == null && lookingForRawString != null) {
            lookingFor = Arrays.asList(lookingForRawString.split(SEPARATOR_LOOKING_FOR));
        }
        return lookingFor;
    }

    public void setLookingFor(List<String> lookingFor) {
        this.lookingFor = lookingFor;
        if (lookingFor != null) {
            String lookingForText = "";
            for (String lookingForElement : lookingFor) {
                lookingForText += SEPARATOR_LOOKING_FOR + lookingForElement;
            }
            setLookingForRawString(lookingForText);
        }
    }

    public String getLookingForRawString() {
        return lookingForRawString;
    }

    public void setLookingForRawString(String lookingForRawString) {
        this.lookingForRawString = lookingForRawString;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAdministrativeArea() {
        return administrativeArea;
    }

    public void setAdministrativeArea(String administrativeArea) {
        this.administrativeArea = administrativeArea;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
        if (avatar != null) {
            AvatarVariants variants = avatar.getVariants();
            if (variants != null) {
                setAvatarLink(variants.getMedium());
            }
        }
    }

    @JsonIgnore
    public Picture getAvatarPicture() {
        return avatar.getAsPicture(Member.this);
    }

    public String getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(String metaInfo) {
        this.metaInfo = metaInfo;
    }

    @JsonIgnore
    public String getAvatarLink() {
        return avatarLink;
    }

    @JsonIgnore
    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            public boolean hasIgnoreMarker(AnnotatedMember m) {
                return m.getDeclaringClass() == BaseModel.class || super.hasIgnoreMarker(m);
            }
        }).writeValueAsString(this);
    }

    public void mergeSave() {
        Member savedMember = Member.loadMember(id);
        if (savedMember != null) {
            savedMember.setAvatar(getAvatar());
            savedMember.setMetaInfo(getMetaInfo());
            savedMember.setNickname(getNickname());
            if (accessToken != null && accessToken.trim().length() != 0) {
                savedMember.setAccessToken(accessToken);
            }
            if (refreshToken != null && refreshToken.trim().length() != 0) {
                savedMember.setRefreshToken(refreshToken);
            }
            savedMember.save();
        } else {
            save();
        }
    }

    @Override
    public boolean save() {
        Member savedMember = Member.loadMember(id);
        if (savedMember != null) {
            setAccessToken(savedMember.getAccessToken());
            setRefreshToken(savedMember.getRefreshToken());
        }
        return super.save();
    }
}
