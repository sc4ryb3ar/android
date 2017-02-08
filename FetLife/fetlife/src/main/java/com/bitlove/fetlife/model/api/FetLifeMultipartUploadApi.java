package com.bitlove.fetlife.model.api;

import com.bitlove.fetlife.model.pojos.AuthBody;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.model.pojos.Feed;
import com.bitlove.fetlife.model.pojos.Friend;
import com.bitlove.fetlife.model.pojos.FriendRequest;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Message;
import com.bitlove.fetlife.model.pojos.Token;
import com.bitlove.fetlife.model.pojos.User;
import com.bitlove.fetlife.model.pojos.VideoUploadResult;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

public interface FetLifeMultipartUploadApi {

    @Multipart
    @POST("/api/v2/me/pictures")
    Call<ResponseBody> uploadPicture(@Header("Authorization") String authHeader, @Part("picture\"; filename=\"android_app.png\" ") RequestBody picture, @Part("is_avatar") RequestBody isAvatar, @Part("only_friends") RequestBody friendsOnly, @Part("caption") RequestBody caption, @Part("is_of_or_by_user") RequestBody isFromUser);
    //TODO: solve dynamic file name
    //https://github.com/square/retrofit/issues/1063

    @Multipart
    @PUT("/api/v2/me/videos/uploads/{video_upload_id}")
    Call<ResponseBody> uploadVideoPart(@Header("Authorization") String authHeader, @Path("video_upload_id") String videoUploadId, @Part("file\"; filename=\"android_app.part\" ") RequestBody video, @Part("number") RequestBody number);
    //TODO: solve dynamic file name
    //https://github.com/square/retrofit/issues/1063

}
