package com.bitlove.fetlife.model.api;

import android.net.Uri;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.AuthBody;
import com.bitlove.fetlife.model.pojos.Conversation;
import com.bitlove.fetlife.model.pojos.Feed;
import com.bitlove.fetlife.model.pojos.Friend;
import com.bitlove.fetlife.model.pojos.FriendRequest;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Message;
import com.bitlove.fetlife.model.pojos.Token;
import com.bitlove.fetlife.model.pojos.User;
import com.facebook.common.util.UriUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.Header;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

public class OfflineTestApi implements FetLifeApi {

    private final FetLifeApplication fetLifeApplication;

    public OfflineTestApi(FetLifeApplication fetLifeApplication) {
        this.fetLifeApplication = fetLifeApplication;
    }

    @Override
    public Call<Token> login(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("redirect_uri") String redirectUrl, @Body() AuthBody authBody) {
        return createOfflineObjectCall("login", Token.class);
    }

    @Override
    public Call<Token> refreshToken(@Query("client_id") String clientId, @Field("client_secret") String clientSecret, @Field("redirect_uri") String redirectUrl, @Field("grant_type") String grantType, @Field("refresh_token") String refreshToken) {
        return null;
    }

    @Override
    public Call<User> getMe(@Header("Authorization") String authHeader) {
        return createOfflineObjectCall("me", User.class);
    }

    @Override
    public Call<List<Conversation>> getConversations(@Header("Authorization") String authHeader, @Query("order_by") String orderBy, @Query("limit") int limit, @Query("page") int page) {
        return createOfflineListCall("conversations",Conversation[].class);
    }

    @Override
    public Call<Conversation> getConversation(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId) {
        return createOfflineObjectCall("conversation", Conversation.class);
    }

    @Override
    public Call<List<Friend>> getFriends(@Header("Authorization") String authHeader, @Query("limit") int limit, @Query("page") int page) {
        return createOfflineListCall("friends",Friend[].class);
    }

    @Override
    public Call<List<Message>> getMessages(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Query("since_id") String sinceMessageId, @Query("until_id") String untilMessageId, @Query("limit") int limit) {
        if (sinceMessageId != null) {
            return createListCall(new ArrayList<Message>());
        }
        return createOfflineListCall("messages",Message[].class);
    }

    @Override
    public Call<Member> getMember(@Header("Authorization") String authHeader, @Path("memberId") String conversationId) {
        return null;
    }

    @Override
    public Call<Message> postMessage(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Field("body") String body) {
        return null;
    }

    @Override
    public Call<ResponseBody> setMessagesRead(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Field("ids") String[] ids) {
        return createCall(null);
    }

    @Override
    public Call<Conversation> postConversation(@Header("Authorization") String authHeader, @Field("user_id") String userId, @Field("subject") String subject, @Field("body") String body) {
        return null;
    }

    @Override
    public Call<List<FriendRequest>> getFriendRequests(@Header("Authorization") String authHeader, @Query("limit") int limit, @Query("page") int page) {
        return createOfflineListCall("friendrequests",FriendRequest[].class);
    }

    @Override
    public Call<FriendRequest> acceptFriendRequests(@Header("Authorization") String authHeader, @Path("friendRequestId") String friendRequestId) {
        return null;
    }

    @Override
    public Call<FriendRequest> removeFriendRequests(@Header("Authorization") String authHeader, @Path("friendRequestId") String friendRequestId) {
        return null;
    }

    @Override
    public Call<FriendRequest> createFriendRequest(@Header("Authorization") String authHeader, @Field("member_id") String friendId) {
        return null;
    }

    @Override
    public Call<ResponseBody> uploadPicture(@Header("Authorization") String authHeader, @Part("picture\"; filename=\"android_app.png\" ") RequestBody picture, @Part("is_avatar") RequestBody isAvatar, @Part("only_friends") RequestBody friendsOnly, @Part("caption") RequestBody caption, @Part("is_of_or_by_user") RequestBody isFromUser) {
        return null;
    }

    @Override
    public Call<Feed> getFeed(@Header("Authorization") String authHeader, @Query("limit") int limit, @Query("page") int page) {
        return createOfflineObjectCall("feed", Feed.class);
    }

    @Override
    public Call<ResponseBody> putLove(@Header("Authorization") String authHeader, @Path("content_id") String contentId, @Path("content_type") String contentType) {
        return null;
    }

    @Override
    public Call<ResponseBody> deleteLove(@Header("Authorization") String authHeader, @Path("content_id") String contentId, @Path("content_type") String contentType) {
        return null;
    }

    private <T> Call<T> createOfflineObjectCall(String name, Class<T> resultClass) {
        try {
            String json = readJsonAsset(name);
            json = addLocalImageUris(json);
            ObjectMapper mapper = new ObjectMapper();
            return createCall(mapper.readValue(json, resultClass));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private <T> Call<List<T>> createOfflineListCall(String name, Class<T[]> arrayClass) {
        try {
            String json = readJsonAsset(name);
            json = addLocalImageUris(json);
            ObjectMapper mapper = new ObjectMapper();
            T[] array = mapper.readValue(json, arrayClass);
            return createListCall(Arrays.asList(array));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private String addLocalImageUris(String json) {
        Pattern pattern = Pattern.compile("locale://[a-zA-Z0-9]*");
        Matcher matcher = pattern.matcher(json);
        StringBuffer stringBuffer = new StringBuffer(json.length());
        while (matcher.find()) {
            String found = matcher.group();
            found = found.substring("locale://".length());
            int drawableId = fetLifeApplication.getResources().getIdentifier(found, "drawable", fetLifeApplication.getPackageName());
            Uri drawableUri = new Uri.Builder().scheme(UriUtil.LOCAL_RESOURCE_SCHEME).path(String.valueOf(drawableId)).build();

            matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(drawableUri.toString()));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private String readJsonAsset(String name) throws Exception {
        StringBuilder buf=new StringBuilder();
        InputStream json = fetLifeApplication.getAssets().open("offline_test_data/json/"+name+".json");
        BufferedReader in= new BufferedReader(new InputStreamReader(json, "UTF-8"));
        String str;
        while ((str=in.readLine()) != null) {
            buf.append(str);
        }
        in.close();
        return buf.toString();
    }

    private <T> Call<T> createCall(final T object) {
        return new Call<T>() {
            @Override
            public Response<T> execute() throws IOException {
                return Response.success(object);
            }
            public void enqueue(Callback<T> callback) {}
            public void cancel() {}
            public Call<T> clone() {return this;}
        };
    }

    private <T> Call<List<T>> createListCall(final List<T> list) {
        return new Call<List<T>>() {
            @Override
            public Response<List<T>> execute() throws IOException {
                return Response.success(list);
            }
            public void enqueue(Callback<List<T>> callback) {}
            public void cancel() {}
            public Call<List<T>> clone() {return this;}
        };
    }


}
