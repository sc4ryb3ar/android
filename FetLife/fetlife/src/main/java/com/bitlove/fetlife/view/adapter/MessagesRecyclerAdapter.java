package com.bitlove.fetlife.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.api.FetLifeApi;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Conversation_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Message;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Message_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.model.pojos.fetlife.json.FeedEvent;
import com.bitlove.fetlife.model.pojos.fetlife.json.Mention;
import com.bitlove.fetlife.model.pojos.fetlife.json.MessageEntities;
import com.bitlove.fetlife.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.util.ColorUtil;
import com.bitlove.fetlife.util.StringUtil;
import com.bitlove.fetlife.util.UrlUtil;
import com.bitlove.fetlife.view.adapter.feed.FeedItemResourceHelper;
import com.bitlove.fetlife.view.adapter.feed.FeedRecyclerAdapter;
import com.bitlove.fetlife.view.screen.resource.ConversationsActivity;
import com.bitlove.fetlife.view.screen.resource.PictureShareActivity;
import com.bitlove.fetlife.view.screen.resource.profile.ProfileActivity;
import com.bitlove.fetlife.view.widget.AutoAlignGridView;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private static final float PENDING_ALPHA = 0.5f;

    private final String conversationId;
    private List<Message> itemList;
    private Conversation conversation;

    public MessagesRecyclerAdapter(String conversationId) {
        this.conversationId = conversationId;
        loadItems();
    }

    public void refresh() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                //TODO: think of possibility of update only specific items instead of the whole list
                loadItems();
                notifyDataSetChanged();
            }
        });
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        conversation = new Select().from(Conversation.class).where(Conversation_Table.id.is(conversationId)).querySingle();
        itemList = new Select().from(Message.class).where(Message_Table.conversationId.is(conversationId)).orderBy(Message_Table.pending,false).orderBy(Message_Table.date,false).orderBy(Message_Table.id,false).queryList();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Message getItem(int position) {
        return itemList.get(position);
    }

    public Conversation getConversation() {
        return conversation;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int position) {
        Message message = itemList.get(position);
        MessageEntities messageEntities;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            messageEntities = objectMapper.readValue(message.getEntitiesJson(), MessageEntities.class);
        } catch (IOException|NullPointerException e) {
            messageEntities = new MessageEntities();
        }

        CharSequence messageBody = StringUtil.parseHtml(message.getBody().trim());
        SpannableString spannedBody = new SpannableString(messageBody);

        List<Mention> mentions = messageEntities.getMentions();
        for (final Mention mention : mentions) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                public static final long CLICK_OFFSET = 500;
                private long lastClick = 0;
                @Override
                public void onClick(View textView) {
                    if (System.currentTimeMillis() - lastClick > CLICK_OFFSET) {
                        mention.getMember().mergeSave();
                        ProfileActivity.startActivity(FetLifeApplication.getInstance(),mention.getMember().getId());
                    }
                    lastClick = System.currentTimeMillis();
                }
            };
            int endPosition = mention.getOffset() + mention.getLength();
            if (spannedBody.length() >= endPosition) {
                spannedBody.setSpan(clickableSpan, mention.getOffset(), endPosition, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                Crashlytics.log("Mention body:" + spannedBody + " mention: " + mention.getOffset() + "," + mention.getLength());
                Crashlytics.logException(new Exception("Invalid mention position"));
            }
        }

        List<Picture> pictures = messageEntities.getPictures();
        if (pictures.isEmpty()) {
            messageViewHolder.messageEntitiesGrid.setVisibility(View.GONE);
        } else {
            messageViewHolder.messageEntitiesGrid.setVisibility(View.VISIBLE);
            int columnCount = Math.min(4,(pictures.size()+2)/3);
            messageViewHolder.messageEntitiesGrid.setNumColumns(columnCount);
            PictureGridAdapter pictureGridAdapter = new PictureGridAdapter(new FeedRecyclerAdapter.OnFeedItemClickListener() {
                @Override
                public void onMemberClick(Member member) {
                    member.mergeSave();
                    ProfileActivity.startActivity(FetLifeApplication.getInstance(), member.getId());
                }

                @Override
                public void onFeedInnerItemClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, FeedItemResourceHelper feedItemResourceHelper) {

                }

                @Override
                public void onFeedImageClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {

                }

                @Override
                public void onFeedImageLongClick(Story.FeedStoryType feedStoryType, String url, FeedEvent feedEvent, Member targetMember) {

                }

                @Override
                public void onVisitItem(Object object, String url) {
                    UrlUtil.openUrl(FetLifeApplication.getInstance(),url);
                }

                @Override
                public void onShareItem(Object object, String url) {
                    if (!(object instanceof  Picture)) {
                        return;
                    }
                    Picture picture = (Picture) object;
                    if (picture.isOnShareList()) {
                        Picture.unsharePicture(picture);
                    } else {
                        Picture.sharePicture(picture);
                    }
                }

            });
            pictureGridAdapter.setPictures(pictures);
            messageViewHolder.messageEntitiesGrid.setAdapter(pictureGridAdapter);
        }

//        textView.setMovementMethod(LinkMovementMethod.getInstance());
//        textView.setHighlightColor(Color.TRANSPARENT);

        messageViewHolder.messageText.setText(spannedBody);
//        messageViewHolder.subText.setText(message.getSenderNickname() + messageViewHolder.subMessageSeparator + SimpleDateFormat.getDateTimeInstance().format(new Date(message.getDate())));
        messageViewHolder.topText.setText(message.getSenderNickname());
        messageViewHolder.subText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(message.getDate())));

        boolean myMessage = message.getSenderId().equals(messageViewHolder.getSelfMessageId());

        if (myMessage) {
//            messageViewHolder.subText.setGravity(Gravity.RIGHT);
            messageViewHolder.messsageAligner.setGravity(Gravity.LEFT);
//            messageViewHolder.messageContainer.setGravity(Gravity.RIGHT);
            messageViewHolder.messageContainer.setPadding(messageViewHolder.extendedHPadding, messageViewHolder.vPadding, messageViewHolder.hPadding, messageViewHolder.vPadding);
        } else {
//            messageViewHolder.subText.setGravity(Gravity.LEFT);
            messageViewHolder.messsageAligner.setGravity(Gravity.LEFT);
//            messageViewHolder.messageContainer.setGravity(Gravity.LEFT);
            messageViewHolder.messageContainer.setPadding(messageViewHolder.hPadding, messageViewHolder.vPadding, messageViewHolder.extendedHPadding, messageViewHolder.vPadding);
        }

        if (message.getPending()) {
            messageViewHolder.messageText.setTextColor(messageViewHolder.primaryTextColor);
            messageViewHolder.messageText.setAlpha(PENDING_ALPHA);
        } else if (message.getFailed()) {
            messageViewHolder.messageText.setAlpha(1f);
            messageViewHolder.messageText.setTextColor(messageViewHolder.errorTextColor);
        } else {
            messageViewHolder.messageText.setTextColor(messageViewHolder.primaryTextColor);
            messageViewHolder.messageText.setAlpha(1f);
        }
    }

}

class MessageViewHolder extends RecyclerView.ViewHolder {

    private static final int EXTEND_PADDING_MULTIPLIER = 10;

    LinearLayout messsageAligner;
    ViewGroup messageContainer;
    TextView messageText, subText, topText;
    String subMessageSeparator;
    int extendedHPadding, hPadding, vPadding;
    AutoAlignGridView messageEntitiesGrid;

    public String selfMessageId;
    public int primaryTextColor, errorTextColor;


    public MessageViewHolder(View itemView) {
        super(itemView);

        Context context = itemView.getContext();

        subMessageSeparator = context.getResources().getString(R.string.message_sub_separator);

        hPadding = (int) context.getResources().getDimension(R.dimen.listitem_horizontal_margin);
        vPadding = (int) context.getResources().getDimension(R.dimen.listitem_vertical_margin);
        extendedHPadding = (int) context.getResources().getDimension(R.dimen.message_extended_horizontal_margin);

        primaryTextColor = ColorUtil.retrieverColor(context, R.color.text_color_primary);
        errorTextColor = ColorUtil.retrieverColor(context, R.color.text_color_error);

        messsageAligner = itemView.findViewById(R.id.message_aligner);
        messageContainer = itemView.findViewById(R.id.message_container);
        messageText = (TextView) itemView.findViewById(R.id.message_text);
        messageText.setMovementMethod(LinkMovementMethod.getInstance());
        subText = (TextView) itemView.findViewById(R.id.message_sub);
        topText = (TextView) itemView.findViewById(R.id.message_top);

        messageEntitiesGrid = itemView.findViewById(R.id.message_grid_entities);
    }

    public String getSelfMessageId() {
        if (selfMessageId == null) {
            FetLifeApplication fetLifeApplication = (FetLifeApplication) messageContainer.getContext().getApplicationContext();
            Member currentUser = fetLifeApplication.getUserSessionManager().getCurrentUser();
            if (currentUser != null) {
                selfMessageId = currentUser.getId();
            }
        }
        return selfMessageId;
    }
}