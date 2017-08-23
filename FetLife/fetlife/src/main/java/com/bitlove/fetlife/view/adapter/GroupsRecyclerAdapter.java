package com.bitlove.fetlife.view.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.GroupReference;
import com.bitlove.fetlife.model.pojos.fetlife.db.GroupReference_Table;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.model.pojos.fetlife.dbjson.Group_Table;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

public class GroupsRecyclerAdapter extends ResourceListRecyclerAdapter<Group, GroupsViewHolder> {

    private static final String DATE_INTERVAL_SEPARATOR = " - ";
    private static final String LOCATION_SEPARATOR = " - ";

    private final String memberId;
    protected List<Group> itemList;

    public GroupsRecyclerAdapter(String memberId) {
        this.memberId = memberId;
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

    protected void loadItems() {
        //TODO: think of moving to separate thread with specific DB executor
        try {
            List<GroupReference> groupReferences = new Select().from(GroupReference.class).where(GroupReference_Table.userId.is(memberId)).orderBy(OrderBy.fromProperty(GroupReference_Table.id).ascending().collate(Collate.NOCASE)).queryList();
            List<String> groupIds = new ArrayList<>();
            for (GroupReference groupReference : groupReferences) {
                groupIds.add(groupReference.getId());
            }
            itemList = new Select().from(Group.class).where(Group_Table.id.in(groupIds)).orderBy(OrderBy.fromProperty(Group_Table.date).ascending()).queryList();
        } catch (Throwable t) {
            itemList = new ArrayList<>();
        }
    }


    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_profile_group, parent, false);
        return new GroupsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        final Group group = itemList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResourceClickListener.onItemClick(group);
            }
        });
        holder.groupName.setText(group.getName());
        holder.groupDescription.setText(group.getDescription());
        holder.groupDescription.setVisibility(TextUtils.isEmpty(group.getDescription()) ? View.GONE : View.VISIBLE);
        String memberCount = holder.itemView.getContext().getString((group.getMemberCount() == 1 ? R.string.text_group_member_count : R.string.text_group_members_count),group.getMemberCount());
        holder.groupMemberCount.setText(memberCount);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    protected boolean useSwipe() {
        return false;
    }

    @Override
    protected void onItemRemove(GroupsViewHolder viewHolder, RecyclerView recyclerView, boolean swipedRight) {
    }

}

class GroupsViewHolder extends SwipeableViewHolder {

    TextView groupName, groupDescription, groupMemberCount;

    public GroupsViewHolder(View itemView) {
        super(itemView);
        groupName = (TextView) itemView.findViewById(R.id.group_name);
        groupDescription = (TextView) itemView.findViewById(R.id.group_description);
        groupMemberCount = (TextView) itemView.findViewById(R.id.group_member_count);
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