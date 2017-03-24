package com.bitlove.fetlife.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.RelationReference;
import com.bitlove.fetlife.model.pojos.Member;
import com.bitlove.fetlife.model.pojos.Member_Table;
import com.bitlove.fetlife.model.pojos.RelationReference_Table;
import com.facebook.drawee.view.SimpleDraweeView;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RelationsRecyclerAdapter extends ResourceListRecyclerAdapter<Member, RelationViewHolder> {

    private final String memberId;
    private final int relationType;
    private List<Member> itemList;

    public RelationsRecyclerAdapter(String memberId, int relationType, FetLifeApplication fetLifeApplication) {
        super(fetLifeApplication);
        this.memberId = memberId;
        this.relationType = relationType;
        loadItems();
    }

    public void refresh() {
        loadItems();
        //TODO: think of possibility of update only specific items instead of the whole list
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            List<RelationReference> relationReferences = new Select().from(RelationReference.class).where(RelationReference_Table.userId.is(memberId)).and(RelationReference_Table.relationType.is(relationType)).orderBy(OrderBy.fromProperty(RelationReference_Table.nickname).ascending().collate(Collate.UNICODE)).queryList();
            List<String> relationIds = new ArrayList<>();
            for (RelationReference relationReference : relationReferences) {
                relationIds.add(relationReference.getId());
            }
            itemList = new Select().from(Member.class).where(Member_Table.id.in(relationIds)).orderBy(OrderBy.fromProperty(Member_Table.nickname).ascending().collate(Collate.UNICODE)).queryList();
            final Collator coll = Collator.getInstance();
            coll.setStrength(Collator.IDENTICAL);
            Collections.sort(itemList, new Comparator<Member>() {
                @Override
                public int compare(Member member, Member member2) {
                    //Workaround to match with DB sorting
                    String nickname1 = member.getNickname().replaceAll("_","z");
                    String nickname2 = member2.getNickname().replaceAll("_","z");
                    return coll.compare(nickname1,nickname2);
                }
            });
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Member getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public RelationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_friend, parent, false);
        return new RelationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RelationViewHolder relationViewHolder, int position) {

        final Member friend = itemList.get(position);

        relationViewHolder.headerText.setText(friend.getNickname());
        relationViewHolder.upperText.setText(friend.getMetaInfo());

//        relationViewHolder.dateText.setText(SimpleDateFormat.getDateTimeInstance().format(new Date(friend.getDate())));

        relationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onItemClick(friend);
                }
            }
        });

        relationViewHolder.avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onResourceClickListener != null) {
                    onResourceClickListener.onAvatarClick(friend);
                }
            }
        });

        relationViewHolder.avatarImage.setImageResource(R.drawable.dummy_avatar);
        String avatarUrl = friend.getAvatarLink();
        relationViewHolder.avatarImage.setImageURI(avatarUrl);
//        imageLoader.loadImage(relationViewHolder.itemView.getContext(), avatarUrl, relationViewHolder.avatarImage, R.drawable.dummy_avatar);
    }

    @Override
    protected void onItemRemove(RelationViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {

    }
}

class RelationViewHolder extends SwipeableViewHolder {

    SimpleDraweeView avatarImage;
    TextView headerText, upperText, dateText;

    public RelationViewHolder(View itemView) {
        super(itemView);

        headerText = (TextView) itemView.findViewById(R.id.friend_header);
        upperText = (TextView) itemView.findViewById(R.id.friend_upper);
        dateText = (TextView) itemView.findViewById(R.id.friend_right);
        avatarImage = (SimpleDraweeView) itemView.findViewById(R.id.friend_icon);
    }

    @Override
    public View getSwipeableLayout() {
        return null;
    }

    @Override
    public View getSwipeRightBackground() {
        return null;
    }

    @Override
    public View getSwipeLeftBackground() {
        return null;
    }
}
